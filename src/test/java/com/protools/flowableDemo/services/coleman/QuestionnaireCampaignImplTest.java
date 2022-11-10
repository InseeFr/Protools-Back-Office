package com.protools.flowableDemo.services.coleman;

import com.protools.flowableDemo.dto.ContextAndVariables;
import com.protools.flowableDemo.enums.Context;
import com.protools.flowableDemo.services.authentification.KeycloakService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireCampaignImplTest {
    @Mock
    private KeycloakService keycloakService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private QuestionnaireCampaignImpl questionnaireCampaign = new QuestionnaireCampaignImpl();

    @Test
    public void givenMockingIsDoneByMockito_whenContextCreationIsCalled_shouldPostContext() throws Exception {
        String colemanQuestionnaireUri = "https://coleman-questionnaire";

        Field field = questionnaireCampaign.getClass().getDeclaredField("colemanQuestionnaireUri");
        field.setAccessible(true);
        field.set(questionnaireCampaign, colemanQuestionnaireUri);

        String token = "token-value";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        QuestionnaireCampaignContext context = new QuestionnaireCampaignContext(
                "id", "label", new ContextAndVariables(Context.household, List.of()), List.of("id"));

        Mockito
                .when(keycloakService.getContextReferentialToken())
                .thenReturn(token);

        questionnaireCampaign.createContext(context);

        Mockito.verify(restTemplate).exchange(
                colemanQuestionnaireUri + "/campaigns",
                HttpMethod.POST,
                new HttpEntity<>(context, headers),
                QuestionnaireCampaignContext.class);
    }
}

