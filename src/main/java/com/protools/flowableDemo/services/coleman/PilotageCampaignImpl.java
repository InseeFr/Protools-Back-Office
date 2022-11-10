package com.protools.flowableDemo.services.coleman;

import com.protools.flowableDemo.services.authentification.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PilotageCampaignImpl implements PilotageCampaign {

    @Value("${fr.insee.coleman.pilotage.uri:#{null}}")
    private String colemanPilotageUri;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void createContext(PilotageCampaignContext context) throws Exception {
        String token = keycloakService.getContextReferentialToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PilotageCampaignContext> request = new HttpEntity<>(context, headers);

        String uri = colemanPilotageUri + "/campaigns";

        restTemplate.exchange(uri, HttpMethod.POST, request, PilotageCampaignContext.class);
    }

}
