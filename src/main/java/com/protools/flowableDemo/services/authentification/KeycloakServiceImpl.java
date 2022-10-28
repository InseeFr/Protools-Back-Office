package com.protools.flowableDemo.services.authentification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${fr.insee.keycloak.auth.server.uri:#{null}}")
    private String authServerUri;

    @Value("${fr.insee.keycloak.realm:#{null}}")
    private String realm;

    @Value("${fr.insee.keycloak.client.id:#{null}}")
    private String clientId;

    @Value("${fr.insee.keycloak.client.secret:#{null}}")
    private String clientSecret;

    private final AtomicReference<Token> token = new AtomicReference<>(new Token("", -1));

    @Override
    public String getContextReferentialToken() throws Exception {
        if (System.currentTimeMillis() >= token.get().endValidityTimeMillis) {
            refreshToken();
        }
        return token.get().value;
    }

    private void refreshToken() throws Exception {
        String uri = authServerUri + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        long endValidityTimeMillis = System.currentTimeMillis();

        ResponseEntity<KeycloakResponse> response = restTemplate.exchange(
                uri, HttpMethod.POST, entity, KeycloakResponse.class);

        KeycloakResponse body = response.getBody();

        if (body == null) {
            throw new Exception("Could not retrieve access token from keycloak");
        }

        endValidityTimeMillis += TimeUnit.SECONDS.toMillis(body.getExpires_in());

        token.set(new Token(body.getAccess_token(), endValidityTimeMillis));
    }

}
