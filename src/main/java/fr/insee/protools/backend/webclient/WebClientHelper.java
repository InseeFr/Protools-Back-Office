package fr.insee.protools.backend.webclient;

import fr.insee.protools.backend.webclient.configuration.APIProperties;
import fr.insee.protools.backend.webclient.configuration.ApiConfigProperties;
import fr.insee.protools.backend.webclient.exception.ApiNotConfiguredException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient4xxException;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient5xxException;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.util.EnumMap;

/**
 * Helper class for WebClient
 */
@Component
@Slf4j
public class WebClientHelper {

        private WebClient.Builder webClientBuilder;
        @Autowired private KeycloakService keycloakService;
        @Autowired private ApiConfigProperties apiConfigProperties;


        private EnumMap<ApiConfigProperties.KNOWN_API,WebClient>  initializedClients = new EnumMap<>(ApiConfigProperties.KNOWN_API.class);

        public WebClientHelper() {
                webClientBuilder = WebClient.builder()
                        .defaultStatusHandler(HttpStatusCode::is4xxClientError, clientResponse ->
                                clientResponse.headers().contentType().equals(MediaType.APPLICATION_JSON)?
                                        clientResponse.bodyToMono(ErrorResponse.class)
                                        .flatMap(error ->
                                                Mono.error(new WebClient4xxException(error.getBody().toString(),error.getStatusCode()))
                                        )
                                        :
                                        Mono.error(new WebClient4xxException("response Type="+ clientResponse.headers().contentType(),clientResponse.statusCode()))
                        )
                        .defaultStatusHandler(HttpStatusCode::is5xxServerError, clientResponse ->
                                clientResponse.headers().contentType().equals(MediaType.APPLICATION_JSON)?
                                        clientResponse.bodyToMono(ErrorResponse.class)
                                                .flatMap(error ->
                                                        Mono.error(new WebClient5xxException(error.getBody().toString()))
                                                )
                                        :
                                        Mono.error(new WebClient5xxException("response Type="+ clientResponse.headers().contentType()))
                        )
                    .clientConnector(
                        new ReactorClientHttpConnector(
                        HttpClient.create()
                            //Handles a proxy conf passed on system properties
                            .proxyWithSystemProperties()
                            //enable logging of request/responses
                            //configurable in properties as if it was this class logers
                            .wiretap(this.getClass().getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)));
        }
        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         * @return
         */
        public WebClient getWebClient() {
                return webClientBuilder
                    .build();
        }

        /**
         * init a new WebClient proxy aware (default one ignore system proxy)
         * with increased buffer size to 20Mb
         * @return
         */
        public WebClient getWebClientForFile() {
                return webClientBuilder
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024))
                        .build();
        }

        /**
         * Get a webclient preconfigured for proxy and able to get the JWT token required for authentification
         * @param api the client will connect to this api
         * @return preconfigured WebClient for the api
         */
        public WebClient getWebClient(ApiConfigProperties.KNOWN_API api) {
                APIProperties apiProperties = apiConfigProperties.getAPIProperties(api);
                if(apiProperties==null){
                        throw new ApiNotConfiguredException(String.format("API %s is not configured in properties",api));
                }
                else if(!apiProperties.getEnabled()){
                        throw new ApiNotConfiguredException(String.format("API %s is disabled in properties",api));
                }
                return initializedClients.computeIfAbsent(api,
                    knownApi ->
                        webClientBuilder
                            .defaultHeaders(new KeycloakHeadersConsumerJSON(apiProperties.getRealm(), keycloakService))
                            .baseUrl(apiProperties.getUrl())
                            .build());
        }
}
