package com.protools.flowableDemo.services.coleman;

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

@ExtendWith(MockitoExtension.class)
public class PilotageCampaignImplTest {
    @Mock
    private KeycloakService keycloakService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PilotageCampaignImpl pilotageCampaign = new PilotageCampaignImpl();

    @Test
    public void givenMockingIsDoneByMockito_whenContextCreationIsCalled_shouldPostContext() throws Exception {
        String colemanPilotageUri = "https://coleman-pilotage";

        Field field = pilotageCampaign.getClass().getDeclaredField("colemanPilotageUri");
        field.setAccessible(true);
        field.set(pilotageCampaign, colemanPilotageUri);

        String token = "token-value";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        PilotageCampaignContext context = new PilotageCampaignContext(
                "id", "label", 0L, 0L);

        Mockito
                .when(keycloakService.getContextReferentialToken())
                .thenReturn(token);

        pilotageCampaign.createContext(context);

        Mockito.verify(restTemplate).exchange(
                colemanPilotageUri + "/campaigns",
                HttpMethod.POST,
                new HttpEntity<>(context, headers),
                PilotageCampaignContext.class);
    }
}

