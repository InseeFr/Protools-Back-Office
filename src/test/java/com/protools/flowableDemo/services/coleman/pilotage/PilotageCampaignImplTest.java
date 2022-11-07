package com.protools.flowableDemo.services.coleman.pilotage;

import com.protools.flowableDemo.services.authentification.KeycloakService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
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
    public void givenMockingIsDoneByMockito_whenExchangeIsCalled_shouldReturnMockedObject() throws Exception {
        String colemanPilotageUri = "https://coleman-pilotage";

        Field field = pilotageCampaign.getClass().getDeclaredField("colemanPilotageUri");
        field.setAccessible(true);
        field.set(pilotageCampaign, colemanPilotageUri);

        String token = "token-value";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        PilotageCampaignContext context = new PilotageCampaignContext(
                "id", "label", 0L, 0L);

        HttpEntity<PilotageCampaignContext> request = new HttpEntity<>(context, headers);

        String uri = colemanPilotageUri + "/campaigns";

        Mockito
                .when(keycloakService.getContextReferentialToken())
                .thenReturn(token);

        Mockito
                .when(restTemplate.exchange(uri, HttpMethod.POST, request, PilotageCampaignContext.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        pilotageCampaign.createContext(context);
    }
}

