package fr.insee.protools.backend.service.meshuggah;


import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.restclient.RestClientHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.insee.protools.backend.restclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_MESHUGGAH;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeshuggahServiceImpl implements IMeshuggahService{

    private final RestClientHelper restClientHelper;

    @Override
    public void postContext(String campaignId, JsonNode contextRootNode) {
        log.trace("postContext: campaignId={}",campaignId);
        var response = restClientHelper.getRestClient(KNOWN_API_MESHUGGAH)
                .post()
                .uri("/context")
                .body(contextRootNode)
                .retrieve()
                .body(String.class);
        log.trace("postContext: campaignId={} - response={} ",campaignId,response);
    }

    @Override
    public void postCommunicationRequest(String campaignId, String communicationId, List<JsonNode> list) {
        log.trace("postCommunicationRequest: campaignId={} - communicationId={}",campaignId,communicationId);
        var response = restClientHelper.getRestClient(KNOWN_API_MESHUGGAH)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/communication-request")
                        .queryParam("communication_id", communicationId)
                        .build())
                .body(list)
                .retrieve()
                .body(String.class);
        log.trace("postCommunicationRequest: campaignId={} - communicationId={} - response={} ",campaignId,communicationId,response);
    }
}
