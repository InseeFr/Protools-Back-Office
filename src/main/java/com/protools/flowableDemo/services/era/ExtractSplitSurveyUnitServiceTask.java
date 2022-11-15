package com.protools.flowableDemo.services.era;

import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExtractSplitSurveyUnitServiceTask implements JavaDelegate {
    Logger logger = LoggerFactory.getLogger(ExtractSplitSurveyUnitServiceTask.class);

    @Value("${fr.insee.era.api}")
    private String eraUrl;
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        logger.info("\t >> Extract Survey Unit and Split response into two JSON Service Task <<  ");
        Integer unitID = (Integer) delegateExecution.getVariableLocal("unitID");
        JSONObject surveyUnitInfo = extractSurveyUnit(unitID);
        String questionnaireKey = "questionnaire";
        JSONObject questionnaireObject = new JSONObject();

        questionnaireObject.append(questionnaireKey, surveyUnitInfo.remove(questionnaireKey));

        delegateExecution.setVariableLocal("questionnaireColemanData",questionnaireObject);
        delegateExecution.setVariableLocal("pilotageColemmanData",surveyUnitInfo);
    }

    public JSONObject extractSurveyUnit(Integer unitID){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(eraUrl+"/extraction-survey-unit/"+unitID))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .GET()
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        logger.info("\t \t >>> Get Survey Unit info for unit  : " + unitID + " << ");
        return jsonResponse;
    }

}
