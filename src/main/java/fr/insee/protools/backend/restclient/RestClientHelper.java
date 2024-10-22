package fr.insee.protools.backend.restclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.protools.backend.restclient.configuration.APIProperties;
import fr.insee.protools.backend.restclient.configuration.ApiConfigProperties;
import fr.insee.protools.backend.restclient.configuration.exception.RequestTimeoutHttpClientErrorException;
import fr.insee.protools.backend.restclient.configuration.exception.TooEarlyHttpClientErrorException;
import fr.insee.protools.backend.restclient.exception.ApiNotConfiguredBPMNError;
import fr.insee.protools.backend.restclient.exception.KeycloakTokenConfigBPMNError;
import fr.insee.protools.backend.restclient.exception.runtime.HttpClient4xxBPMNError;
import fr.insee.protools.backend.restclient.exception.runtime.HttpClient5xxBPMNError;
import fr.insee.protools.backend.restclient.keycloak.KeycloakService;
import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ReactorNettyClientRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * Helper class for RestClient
 */
@Component
@Slf4j
public class RestClientHelper {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final KeycloakService keycloakService;
    private final ApiConfigProperties apiConfigProperties;
    private final EnumMap<ApiConfigProperties.KNOWN_API, RestClient> initializedClients = new EnumMap<>(ApiConfigProperties.KNOWN_API.class);

    public RestClientHelper(KeycloakService keycloakService, ApiConfigProperties apiConfigProperties) {
        this.keycloakService = keycloakService;
        this.apiConfigProperties = apiConfigProperties;
    }

    //analyse a single token to retrieve roles
    private static String analyseToken(String token) {
        String result;
        String[] chunks = token.split("\\.");
        if (chunks.length < 2) {
            return "Token size is incorrect. It should contain at least one dot";
        }
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        try {
            JsonNode tokenPayloadNode = objectMapper.readTree(payload);
            JsonNode roles = tokenPayloadNode.path("realm_access").path("roles");
            if (roles.isEmpty()) {
                result = "No Role found in token";
            } else {
                result = roles.toString();
            }
        } catch (JsonProcessingException e) {
            result = "Exception during json token parsing";
        }
        return result;
    }

    /**
     * special version of NestedRuntimeException.contains accepting a list of types :
     * Check whether the Ex exception contains an exception of the given list of types:
     * either it is of the given class itself or it contains a nested cause of one of these types.
     *
     * @param ex      : The exception
     * @param exTypes : The searched types
     * @return true if a matching exception type has been found
     */
    @SuppressWarnings("java:S3776")
    public static boolean containsCauseOfType(Exception ex, @Nullable List<Class<?>> exTypes) {
        if (exTypes == null || exTypes.isEmpty()) {
            return false;
        } else {
            if (exTypes.stream().anyMatch(exType -> exType.isInstance(ex))) {
                return true;
            }

            //Current exception is not of the configured types ; check parents
            Throwable cause = ex.getCause();
            if (cause == ex) {
                return false;
            } else {
                while (cause != null) {
                    Throwable finalCause = cause; //to be used in lambda
                    if (exTypes.stream().anyMatch(exType -> exType.isInstance(finalCause))) {
                        return true;
                    }

                    if (cause.getCause() == cause) {
                        break;
                    }

                    cause = cause.getCause();
                }

                return false;
            }
        }
    }

    //I cannot have a single builder and store it in a private variable because every call to .filter(...) append a new filter to the builder
    //TODO: Still true for Restclient?
    public RestClient.Builder getBuilder(Duration exchangeTimeout, Duration readTimeout) {
        ReactorNettyClientRequestFactory requestFactory = new ReactorNettyClientRequestFactory(HttpClient.create()
                //Handles a proxy conf passed on system properties
                .proxyWithSystemProperties()
                //enable logging of request/responses
                //configurable in properties as if it was this class logers
                .wiretap(this.getClass().getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                // Increase the buffer size and adjust Netty options for large payloads
                .option(ChannelOption.SO_RCVBUF, 1048576 * 10) // Adjust receive buffer size
                .compress(true));
        requestFactory.setExchangeTimeout(exchangeTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .defaultStatusHandler(HttpStatusCode::isError, this::handleError)
                .requestFactory(requestFactory)
                .requestInterceptor(
                        (request, body, execution) -> {
                            ClientHttpResponse response = execution.execute(request, body);
                            if (response.getStatusCode() == HttpStatus.REQUEST_TIMEOUT)
                                throw new RequestTimeoutHttpClientErrorException();
                            if (response.getStatusCode() == HttpStatus.TOO_EARLY)
                                throw new TooEarlyHttpClientErrorException();
                            return response;
                        });
    }

    private void handleError(HttpRequest httpRequest, ClientHttpResponse clientResponse) throws IOException {
        String errorMsg = String.format("request=[%s %s] - statusCode=[%s]",
                httpRequest.getMethod(), httpRequest.getURI(), clientResponse.getStatusCode());
        if (clientResponse.getStatusCode().equals(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()))) {
            errorMsg = "HttpStatus.UNAUTHORIZED. WWW-Authenticate=[" + String.join("", clientResponse.getHeaders().get("WWW-Authenticate")) + "]";
        }

        String finalErrorMsg = errorMsg;
        String errorMessage = StreamUtils.copyToString(clientResponse.getBody(), StandardCharsets.UTF_8);
        if (errorMessage.isBlank()) {
            errorMessage = "No error message provided by API";
        }
        if (clientResponse.getStatusCode().is4xxClientError()) {
            throw new HttpClient4xxBPMNError(finalErrorMsg + " - " + errorMessage, clientResponse.getStatusCode());
        } else {
            throw new HttpClient5xxBPMNError(finalErrorMsg + " - " + errorMessage);
        }
    }

    /**
     * init a new RestClient proxy aware (default one ignore system proxy)
     */
    public RestClient getRestClient() {
        return getBuilder(Duration.ofSeconds(5), Duration.ofSeconds(10))
                .build();
    }

    /**
     * Get a RestClient preconfigured for proxy and able to get the JWT token required for authentication
     *
     * @param api the client will connect to this api
     * @return preconfigured RestClient for the api
     */
    public RestClient getRestClient(ApiConfigProperties.KNOWN_API api) {
        APIProperties apiProperties = apiConfigProperties.getAPIProperties(api);
        if (apiProperties == null) {
            throw new ApiNotConfiguredBPMNError(String.format("API %s is not configured in properties", api));
        } else if (Boolean.FALSE.equals(apiProperties.getEnabled())) {
            throw new ApiNotConfiguredBPMNError(String.format("API %s is disabled in properties", api));
        }
        return initializedClients.computeIfAbsent(api,
                knownApi ->
                        getBuilder(apiProperties.getExchangeTimeout(), apiProperties.getReadTimeout())
                                .baseUrl(apiProperties.getUrl())
                                .requestInitializer(new RestClientKeycloakHeadersInitializer(keycloakService, apiProperties.getAuth()))
                                .build());
    }

    public Map<String, String> getTokenDetailsByAPI() {
        Map<String, String> result = new HashMap<>();
        for (var api : ApiConfigProperties.KNOWN_API.values()) {
            try {
                APIProperties apiProperties = apiConfigProperties.getAPIProperties(api);
                if (apiProperties == null) {
                    throw new ApiNotConfiguredBPMNError(String.format("API %s is not configured in properties", api));
                } else if (Boolean.FALSE.equals(apiProperties.getEnabled())) {
                    throw new ApiNotConfiguredBPMNError(String.format("API %s is disabled in properties", api));
                }
                var token = keycloakService.getToken(apiProperties.getAuth());
                if (token != null && !token.isBlank()) {
                    String details = analyseToken(token);
                    result.put(api.name(), details);

                }
            } catch (KeycloakTokenConfigBPMNError | ApiNotConfiguredBPMNError e) {
                result.put(api.name(), e.getMessage());
            } catch (Exception e) {
                result.put(api.name(), "Internal error with token");
            }
        }
        return result;
    }

    /**
     * @return A json with the configuration of the APIs handled by protools
     */
    public JsonNode getAPIConfigDetails() {
        ArrayNode rootNode = objectMapper.createArrayNode();
        for (var api : ApiConfigProperties.KNOWN_API.values()) {
            APIProperties apiProperties = apiConfigProperties.getAPIProperties(api);
            ObjectNode apiNode = objectMapper.valueToTree(apiProperties);
            apiNode.put("name", api.name());
            rootNode.add(apiNode);
        }
        return rootNode;
    }
}
