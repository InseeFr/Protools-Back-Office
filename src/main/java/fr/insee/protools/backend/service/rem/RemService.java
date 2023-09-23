package fr.insee.protools.backend.service.rem;

import fr.insee.protools.backend.service.era.dto.CensusJsonDto;
import fr.insee.protools.backend.service.rem.dto.REMSurveyUnitDto;
import fr.insee.protools.backend.service.rem.dto.SuIdMappingJson;
import fr.insee.protools.backend.webclient.WebClientHelper;
import fr.insee.protools.backend.webclient.exception.runtime.WebClient4xxBPMNError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_REM;

@Service
@Slf4j
public class RemService {

    @Autowired WebClientHelper webClientHelper;

    public Long[] getSampleSuIds(Long partitionId) {
        log.debug("getSampleSuIds - partitionId={} ",partitionId);
        try {
            var response = webClientHelper.getWebClient(KNOWN_API_REM)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/survey-units/partitions/{partitionId}/ids")
                            .build(partitionId))
                    .retrieve()
                    .bodyToMono(Long[].class)
                    .block();
            log.trace("partitionIds={} - response={} ", partitionId, response);
            return response;
        }
        catch (WebClient4xxBPMNError e){
            if(e.getHttpStatusCodeError().equals(HttpStatus.NOT_FOUND)){
                String msg=
                        "Error 404/NOT_FOUND during get sample on REM with partitionId="+partitionId
                                + " - msg="+e.getMessage();
                log.error(msg);
                throw new WebClient4xxBPMNError(msg,e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }

    public REMSurveyUnitDto getSurveyUnit(Long surveyUnitId ) {
        log.debug("getSurveyUnit - surveyUnitId ={}",surveyUnitId );
        try {
            var response = webClientHelper.getWebClient(KNOWN_API_REM)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/survey-units/{surveyUnitId}")
                            .queryParam("withExternals", true)
                            .build(surveyUnitId))
                    .retrieve()
                    .bodyToMono(REMSurveyUnitDto.class)
                    .block();
            log.trace("surveyUnitId={} - response={} ", surveyUnitId, response);
            return response;
        }
        catch (WebClient4xxBPMNError e){
            if(e.getHttpStatusCodeError().equals(HttpStatus.NOT_FOUND)){
                String msg=
                        "Error 404/NOT_FOUND during get SU on REM with surveyUnitId="+surveyUnitId
                                + " - msg="+e.getMessage();
                log.error(msg);
                throw new WebClient4xxBPMNError(msg,e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }

    public SuIdMappingJson writeERASUList(long partitionId, List<CensusJsonDto> values) {
        log.debug("writeERASUList - partitionId={}  - values.size={}", partitionId,values==null?0:values.size() );
        try {
            var response =  webClientHelper.getWebClient(KNOWN_API_REM)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/survey-units/households/partitions/{partitionId}/census-upload")
                            .build(partitionId))
                    .bodyValue(values)
                    .retrieve()
                    .bodyToMono(SuIdMappingJson.class)
                    .block();
            log.trace("writeERASUList - partitionId={} - response={} ", partitionId, response);
            return response;
        }
        catch (WebClient4xxBPMNError e){
            if(e.getHttpStatusCodeError().equals(HttpStatus.NOT_FOUND)){
                String msg=
                        "Error 404/NOT_FOUND during REM post census-upload partitionId="+partitionId
                                + " - msg="+e.getMessage();
                log.error(msg);
                throw new WebClient4xxBPMNError(msg,e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }
}
