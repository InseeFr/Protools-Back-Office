package com.protools.flowableDemo.services.authentification;

import org.springframework.stereotype.Service;

@Service
public interface KeycloakService {
    public abstract String getContextReferentialToken() throws Exception;
}
