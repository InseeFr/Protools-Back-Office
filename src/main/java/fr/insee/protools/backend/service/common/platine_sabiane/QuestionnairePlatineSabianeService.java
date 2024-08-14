package fr.insee.protools.backend.service.common.platine_sabiane;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.NomenclatureDto;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.QuestionnaireModelCreateDto;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.campaign.CampaignDto;
import fr.insee.protools.backend.dto.platine_sabiane_questionnaire.surveyunit.SurveyUnitResponseDto;
import fr.insee.protools.backend.httpclients.webclient.WebClientHelper;
import fr.insee.protools.backend.httpclients.exception.runtime.HttpClient4xxBPMNError;
import fr.insee.protools.backend.httpclients.exception.runtime.HttpClient5xxBPMNError;
import fr.insee.protools.backend.httpclients.exception.runtime.HttpClientNullReturnBPMNError;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.insee.protools.backend.httpclients.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE;

public interface QuestionnairePlatineSabianeService {

    //Internal methods
    RestClient restClient();
    org.slf4j.Logger getLogger();

    /** Create a new nomenclature **/
    default void postNomenclature(String nomenclatureId,  String nomenclatureLabel , JsonNode nomenclatureValue) {
        NomenclatureDto dto = new NomenclatureDto(nomenclatureId,nomenclatureLabel,nomenclatureValue);
        NomenclatureDto response =
                restClient()
                        .post()
                        .uri("/api/nomenclature")
                        .body(dto)
                        .retrieve()
                        .body(NomenclatureDto.class);
        getLogger().info("postNomenclature: nomenclatureId={} - response={}",nomenclatureId,response);
        //TODO:  gestion des erreurs (ex: 403...)
    }

    /** Create a new questionnaireModel **/
    default void postQuestionnaireModel(String questionnaireId, String questionnaireLabel, JsonNode questionnaireValue, Set<String> requiredNomenclatures) {
        QuestionnaireModelCreateDto dto =
                new QuestionnaireModelCreateDto(questionnaireId,questionnaireLabel,questionnaireValue,requiredNomenclatures);

        QuestionnaireModelCreateDto response =
                restClient()
                        .post()
                        .uri("/api/questionnaire-models")
                        .body(dto)
                        .retrieve()
                        .body(QuestionnaireModelCreateDto.class);
        getLogger().info("postQuestionnaireModel: questionnaireId={} - response={}",questionnaireId,response);
        //TODO:  gestion des erreurs (ex: 403...)

    }

    /** Get the list of existing nomenclatures */
    default Set<String> getNomenclaturesId() {
        List<String> response = restClient()
                .get()
                .uri("/api/nomenclatures")
                .retrieve()
                .body(List.class);
        getLogger().info("getNomenclaturesId: response= {}",response);
        return (response==null)?new HashSet<>():response.stream().collect(Collectors.toSet());
    }

    /** Checks if the questionnaireModel exists **/
    default boolean questionnaireModelExists(String idQuestionnaireModel) {
        getLogger().info("questionnaireModelExists: idQuestionnaireModel={} ",idQuestionnaireModel);
        boolean modelExists = false;
        try{
            var response = restClient()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/questionnaire/{id}")
                            .build(idQuestionnaireModel))
                    .retrieve().toBodilessEntity();
            if(response==null) {
                throw new HttpClientNullReturnBPMNError("Error while checking if questionnaireModel exists - null result");
            }

            if(response.getStatusCode().is2xxSuccessful()) {
                modelExists = true;
            }
            else if(response.getStatusCode().is4xxClientError()){
                if(response.getStatusCode()==HttpStatus.NOT_FOUND){
                    modelExists=false;
                }
                else{
                    throw new HttpClient4xxBPMNError("Error while checking if questionnaireModel exists ", response.getStatusCode());
                }
            }
            else{
                throw new HttpClient5xxBPMNError("Error while checking if questionnaireModel exists ");
            }
            getLogger().debug("response code={}",response.getStatusCode());
        }
        catch (HttpClient4xxBPMNError e){
            if(e.getHttpStatusCodeError().equals(HttpStatus.NOT_FOUND)){
                modelExists=false;
            }
            else {
                throw e;
            }
        }
        getLogger().info("questionnaireModelExists: idQuestionnaireModel={} - modelExists={}",idQuestionnaireModel,modelExists);
        return modelExists;
    }

    /** Create the campaign **/
    default void postContext(String campaignId,JsonNode contextRootNode) {
        getLogger().trace("postContext: campaignId={}",campaignId);
        //Http Status Codes : https://github.com/InseeFr/Queen-Back-Office/blob/3.5.36-rc/src/main/java/fr/insee/queen/api/controller/CampaignController.java
        // HttpStatus.BAD_REQUEST(400) if campaign already exists
        // HttpStatus.FORBIDDEN (403) if the questionnaire does not exist or is already associated (Request to change it to 409)
        // WARNING : 403 will also be returned if user does not have an authorized role
        try {
            var response = restClient()
                    .post()
                    .uri("/context")
                    .body(contextRootNode)
                    .retrieve()
                    .body(String.class);
            getLogger().info("postContext: idCampaign={} -  response={} ", campaignId, response);
        }
        catch (HttpClient4xxBPMNError e){
            if(e.getHttpStatusCodeError().equals(HttpStatus.FORBIDDEN)){
                String msg=
                        "Error 403/FORBIDEN during Questionnaire postContext."
                                + " It can be caused by a missing permission or if a questionnaire model"
                                +" is already assigned to another campaign."
                                + " msg="+e.getMessage();
                getLogger().error(msg);
                throw new HttpClient4xxBPMNError(msg,e.getHttpStatusCodeError());
            }
            else if(e.getHttpStatusCodeError().equals(HttpStatus.BAD_REQUEST)){
                String msg="Error 400/BAD_REQUEST during Questionnaire postContext."
                                + " One possible cause is that the campaign already exists "
                                + " msg="+e.getMessage();
                getLogger().error(msg);
                throw new HttpClient4xxBPMNError(msg,e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }

    /** Create a survey Unit **/
    default void postSurveyUnit(SurveyUnitResponseDto suDto, String idCampaign) {
        WebClientHelper.logJson("postSurveyUnit: idCampaign="+idCampaign, suDto,getLogger(),Level.DEBUG);
        try {
            var response = restClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/campaign/{id}/survey-unit")
                            .build(idCampaign))
                    .body(suDto)
                    .retrieve()
                    .body(String.class);
            getLogger().info("postSurveyUnit: idCampaign={} - idSu={} - response={} ", idCampaign,suDto.getId(), response);
        }
        catch (HttpClient4xxBPMNError e){
            if(e.getHttpStatusCodeError().equals(HttpStatus.BAD_REQUEST)){
                String msg="Error 400/BAD_REQUEST during Questionnaire postSurveyUnit."
                        + " msg="+e.getMessage();
                getLogger().error(msg);
                throw new HttpClient4xxBPMNError(msg,e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }

    //V2
    default void postSurveyUnits(String idCampaign, List<JsonNode> interrogations) {
        WebClientHelper.logJson("postSurveyUnits: idCampaign=" + idCampaign, interrogations, getLogger(), Level.DEBUG);
        try {
            var response = restClient()
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/campaign/{id}/survey-unit")
                            .build(idCampaign))
                    .body(interrogations)
                    .retrieve()
                    .body(String.class);
            getLogger().info("postSurveyUnits: idCampaign={} - response={} ", idCampaign, response);
        } catch (HttpClient4xxBPMNError e) {
            if (e.getHttpStatusCodeError().equals(HttpStatus.BAD_REQUEST)) {
                String msg = "Error 400/BAD_REQUEST during Questionnaire postSurveyUnits."
                        + " msg=" + e.getMessage();
                getLogger().error(msg);
                throw new HttpClient4xxBPMNError(msg, e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }
}
