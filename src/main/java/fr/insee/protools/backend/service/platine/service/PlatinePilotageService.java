package fr.insee.protools.backend.service.platine.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageCommunicationEventDto;
import fr.insee.protools.backend.restclient.RestClientHelper;
import fr.insee.protools.backend.restclient.exception.runtime.HttpClient4xxBPMNError;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static fr.insee.protools.backend.restclient.RestClientHelper.logJson;
import static fr.insee.protools.backend.restclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlatinePilotageService {

    private final RestClientHelper restClientHelper;

    @Value("${fr.insee.protools.api.platine-pilotage.interrogation.page.size:5000}")
    private int pageSizeGetInterro;

    public void postCommunicationEvent(List<PlatinePilotageCommunicationEventDto> platinePilotageCommunicationEventList) {
        log.trace("postCommunicationEvent: ");
        logJson("postCommunicationEvent ",platinePilotageCommunicationEventList,log, Level.TRACE);

        var response = restClientHelper.getRestClient(KNOWN_API_PLATINE_PILOTAGE)
                .post()
                .uri("/interrogations/communication-events")
                .body(platinePilotageCommunicationEventList)
                .retrieve()
                .body(String.class);
        log.trace("postCommunicationEvent: response={} ",response);
    }

    public void postContext(String campaignId, JsonNode contextRootNode) {
        log.trace("postContext: campaignId={}",campaignId);
        var response = restClientHelper.getRestClient(KNOWN_API_PLATINE_PILOTAGE)
                .post()
                .uri("/context")
                .body(contextRootNode)
                .retrieve()
                .body(String.class);
        log.trace("postContext: campaignId={} - response={} ",campaignId,response);
    }

    public void postInterrogations(String campaignId, List<JsonNode> interrogations) {
        log.trace("postInterrogations: campaignId={}",campaignId);
        logJson("putQuestionings ",interrogations,log,Level.TRACE);
        var response = restClientHelper.getRestClient(KNOWN_API_PLATINE_PILOTAGE)
                .put()
                .uri("/interrogations")
                .body(interrogations)
                .retrieve()
                .body(String.class);
        log.trace("postInterrogations: campaignId={} - response={} ",campaignId,response);
    }


    public PageResponse<JsonNode> getInterrogationToFollowUpPaginated(Long partitionId, long page, Optional<Boolean> isToFollowUp) {
        log.debug("partitionId={} - page={} - pageSizeGetInterro={} - isToFollowUp={}",partitionId,page,pageSizeGetInterro,isToFollowUp);
        ParameterizedTypeReference<PageResponse<JsonNode>> typeReference = new ParameterizedTypeReference<>() { };
        try {
            PageResponse<JsonNode> response = restClientHelper.getRestClient(KNOWN_API_PLATINE_PILOTAGE)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("interrogations")
                            .queryParam("page", page)
                            .queryParam("size", pageSizeGetInterro)
                            .queryParam("partition_id", partitionId)
                            .queryParamIfPresent("follow-up", isToFollowUp)
                            .build(partitionId))
                    .retrieve()
                    .body(typeReference);
            log.trace("partitionId={} - page={} - pageSizeGetInterro={} - response={} ", partitionId,page,pageSizeGetInterro, response.getContent().size());
            return response;
        }
        catch (HttpClient4xxBPMNError e){
            if(e.getHttpStatusCodeError().equals(HttpStatus.NOT_FOUND)){
                String msg=
                        "Error 404/NOT_FOUND during Platine Pilotage getInterrogationToFollowUpPaginated with partitionId="+partitionId
                                + " - msg="+e.getMessage();
                log.error(msg);
                throw new HttpClient4xxBPMNError(msg,e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }
}
