package com.protools.flowableDemo.services.authentification;

import lombok.Data;

@Data
public class KeycloakResponse {
    private final String access_token;

    private final Integer expires_in;
}
