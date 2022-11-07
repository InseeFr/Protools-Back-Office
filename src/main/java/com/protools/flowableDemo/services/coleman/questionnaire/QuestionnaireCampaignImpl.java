package com.protools.flowableDemo.services.coleman.questionnaire;

import com.protools.flowableDemo.services.authentification.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuestionnaireCampaignImpl implements QuestionnaireCampaign {

    @Value("${fr.insee.coleman.questionnaire.uri:#{null}}")
    private String colemanQuestionnaireUri;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void createContext(QuestionnaireCampaignContext context) throws Exception {
        String token = keycloakService.getContextReferentialToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<QuestionnaireCampaignContext> request = new HttpEntity<>(context, headers);

        String uri = colemanQuestionnaireUri + "/campaigns";

        restTemplate.exchange(uri, HttpMethod.POST, request, QuestionnaireCampaignContext.class);
    }

}
