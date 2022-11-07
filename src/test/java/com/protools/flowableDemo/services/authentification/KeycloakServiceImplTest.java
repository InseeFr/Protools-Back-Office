package com.protools.flowableDemo.services.authentification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
public class KeycloakServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KeycloakServiceImpl keycloakService = new KeycloakServiceImpl();

    @Test
    public void givenMockingIsDoneByMockito_whenKeycloakServiceIsCalled_shouldReturnToken() throws Exception {
        String authServerUri = "https://authentification-server";
        String realm = "realm";
        String clientId = "id";
        String clientSecret = "secret";

        Field field;

        field = keycloakService.getClass().getDeclaredField("authServerUri");
        field.setAccessible(true);
        field.set(keycloakService, authServerUri);

        field = keycloakService.getClass().getDeclaredField("realm");
        field.setAccessible(true);
        field.set(keycloakService, realm);

        field = keycloakService.getClass().getDeclaredField("clientId");
        field.setAccessible(true);
        field.set(keycloakService, clientId);

        field = keycloakService.getClass().getDeclaredField("clientSecret");
        field.setAccessible(true);
        field.set(keycloakService, clientSecret);

        String uri = authServerUri + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");
        requestBody.add("client_id", clientId);
        requestBody.add("client_secret", clientSecret);

         HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        String token = "token-value";

        KeycloakResponse keycloakResponse = new KeycloakResponse(token, 1);

        Mockito
                .when(restTemplate.exchange(uri, HttpMethod.POST, entity, KeycloakResponse.class))
                .thenReturn(new ResponseEntity<>(keycloakResponse, HttpStatus.OK));

        String providedToken = keycloakService.getContextReferentialToken();

        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                uri, HttpMethod.POST, entity, KeycloakResponse.class);

        Assertions.assertEquals(token, providedToken);

        keycloakService.getContextReferentialToken();

        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                uri, HttpMethod.POST, entity, KeycloakResponse.class);

        Assertions.assertEquals(token, providedToken);
    }
}

