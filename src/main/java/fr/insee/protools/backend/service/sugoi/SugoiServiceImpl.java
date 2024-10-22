package fr.insee.protools.backend.service.sugoi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.protools.backend.dto.sugoi.User;
import fr.insee.protools.backend.restclient.RestClientHelper;
import fr.insee.protools.backend.restclient.configuration.ApiConfigProperties;
import fr.insee.protools.backend.restclient.configuration.annotation.RestClientRetryable;
import fr.insee.protools.backend.restclient.exception.runtime.HttpClient4xxBPMNError;
import fr.insee.protools.backend.service.exception.SugoiServiceCallBPMNError;
import fr.insee.protools.backend.service.exception.UsernameAlreadyExistsSugoiBPMNError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import static fr.insee.protools.backend.restclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_SUGOI;

@Service
@Slf4j
@RequiredArgsConstructor
public class SugoiServiceImpl implements ISugoiService{

    private static final ApiConfigProperties.KNOWN_API API = KNOWN_API_SUGOI;

    static final String STORAGE = "default";
    private final RestClientHelper restClientHelper;
    private final ObjectMapper objectMapper;
    @Value("${fr.insee.protools.api.sugoi.dmz-account-creation-realm:questionnaire-particuliers}")
    private String realm;

    @Override
    @RestClientRetryable
    public User postCreateUser(User userBody) {
        if(userBody==null){
            String msg="called  SUGOI postCreateUser with empty userBody";
            log.error(msg);
            throw new SugoiServiceCallBPMNError(msg);
        }

        try {
            User userCreated = restClientHelper.getRestClient(API)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/realms/{realm}/storages/{storage}/users")
                            .build(realm, STORAGE))
                    .body(userBody)
                    .retrieve()
                    .body(User.class);
            log.info("postCreateUsers - response={} ", userCreated);
            return userCreated;
        } catch (HttpClient4xxBPMNError e) {
            if (e.getHttpStatusCodeError().equals(HttpStatus.CONFLICT)) {
                String msg =
                        "Error 409/CONFLICT during SUGOI post create users userBody.username=" + userBody.getUsername()
                        + " (check that the username already exists in SUGOI) - msg=" + e.getMessage();
                log.error(msg);
                throw new UsernameAlreadyExistsSugoiBPMNError(msg);
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }

    @Override
    public void postInitPassword(String userId, String password) {
        log.debug("postInitPassword - userId={} begin", userId);
        ObjectNode body = objectMapper.createObjectNode();
        body.put("password", password);
        restClientHelper.getRestClient(API)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/realms/{realm}/users/{id}/init-password")
                        .queryParam("change-password-reset-status", true)
                        .build(realm, userId))
                .body(body)
                .retrieve()
                .toBodilessEntity();
        log.info("postInitPassword - userId={} end", userId);
    }
}
