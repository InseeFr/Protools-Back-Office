package com.protools.flowableDemo.services.coleman.pilotage;

import com.protools.flowableDemo.services.authentification.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PilotageCampaignImpl implements PilotageCampaign {

    @Value("${fr.insee.coleman.pilotage.uri:#{null}}")
    private String colemanPilotageUri;

    @Autowired
    private KeycloakService keycloakService;

    @Override
    public void createContext(PilotageCampaignContext context) throws Exception {
        String token = keycloakService.getContextReferentialToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<PilotageCampaignContext> request = new HttpEntity<>(context, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<PilotageCampaignContext> response = restTemplate.exchange(
                colemanPilotageUri + "/campaigns", HttpMethod.POST, request, PilotageCampaignContext.class);
    }

}
