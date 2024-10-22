package fr.insee.protools.backend.service.platine.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.restclient.RestClientHelper;
import fr.insee.protools.backend.restclient.configuration.ApiConfigProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.insee.protools.backend.logging.LoggingHelper.logJson;
import static fr.insee.protools.backend.restclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_QUESTIONNAIRE;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlatineQuestionnaireServiceImpl implements IPlatineQuestionnaireService{

    private final RestClientHelper restClientHelper;
    private static final ApiConfigProperties.KNOWN_API API= KNOWN_API_PLATINE_QUESTIONNAIRE;

    @Override
    public void postContext(String campaignId, JsonNode contextRootNode) {
        log.trace("postContext: campaignId={}",campaignId);
        var response = restClientHelper.getRestClient(API)
                .post()
                .uri("/context")
                .body(contextRootNode)
                .retrieve()
                .body(String.class);
        log.trace("postContext: campaignId={} - response={} ",campaignId,response);
    }

    @Override
    public void postInterrogations(String campaignId, List<JsonNode> interrogations) {
        log.trace("postInterrogations: campaignId={}",campaignId);
        logJson("postInterrogations ",interrogations,log,Level.TRACE);
        var response = restClientHelper.getRestClient(API)
                .post()
                .uri("/interrogations")
                .body(interrogations)
                .retrieve()
                .body(String.class);
        log.trace("postInterrogations: campaignId={} - response={} ",campaignId,response);
    }

}
