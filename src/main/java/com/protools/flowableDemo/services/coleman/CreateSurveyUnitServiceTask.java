package com.protools.flowableDemo.services.coleman;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.pro.packaged.J;
import liquibase.pro.packaged.S;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreateSurveyUnitServiceTask implements JavaDelegate {
    Logger logger = LoggerFactory.getLogger(CreateSurveyUnitServiceTask.class);

    @Value("${fr.insee.coleman.questionnaire.uri}")
    private String colemanQuestionnaireUri;

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    @Override
    public void execute(DelegateExecution delegateExecution){
        logger.info("\t >> Create Survey Unit into Coleman Pilotage & Questionnaire Service Task <<  ");
        JSONObject questionnaireColemanData = (JSONObject) delegateExecution.getVariableLocal("questionnaireColemanData");
        JSONObject pilotageColemanData = (JSONObject) delegateExecution.getVariableLocal("pilotageColemanData");
        String idCampaign = (String) delegateExecution.getVariableLocal("idCampaign");
        String idUnit = (String) delegateExecution.getVariableLocal("idUnit");

        questionnaireColemanData = transformColemanQuestionnaireData(questionnaireColemanData);
        sendColemanPilotageData(pilotageColemanData,idCampaign);
        sendColemanQuestionnaireData(questionnaireColemanData,idUnit);

    }

    // Send TRANSFORMED data into Coleman Pilotage
    public void sendColemanPilotageData(JSONObject pilotageColemanData, String idCampaign){
        HttpClient client = HttpClient.newHttpClient();

        // Transfo Data Coleman Pilotage
        pilotageColemanData= (JSONObject) pilotageColemanData.get("pilotage");
        List<JSONObject> pilotageColemanDataArray = new ArrayList<>();
        pilotageColemanDataArray.add(pilotageColemanData);

        var objectMapper = new ObjectMapper();
        String requestBodyPilotage = null;
        try {
            requestBodyPilotage = objectMapper
                    .writeValueAsString(pilotageColemanDataArray);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/rest-survey-unit/campaigns/"+ idCampaign + "/survey-units"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyPilotage))
                .build();
        HttpResponse<String> responsePilotage = null;
        try {
            responsePilotage = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("\t \t Coleman Pilotage Response : "+ responsePilotage.statusCode());
    }

    // Send TRANSFORMED data into Coleman Questionnaire
    public void sendColemanQuestionnaireData(JSONObject questionnaireColemanData, String idUnit){
        HttpClient client = HttpClient.newHttpClient();

        var objectMapper = new ObjectMapper();
        String requestBodyQuestionnaire = null;
        try {
            requestBodyQuestionnaire = objectMapper
                    .writeValueAsString(questionnaireColemanData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanQuestionnaireUri+"/api/campaign/"+ idUnit + "/survey-unit"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyQuestionnaire))
                .build();
        HttpResponse<String> responseQuestionnaire = null;
        try {
            responseQuestionnaire = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("\t \t Coleman Pilotage Response : "+ responseQuestionnaire.statusCode());
    }

    public JSONObject transformColemanQuestionnaireData(JSONObject questionnaireColemanData){
        // TODO : Vérifier le format des données
        questionnaireColemanData = (JSONObject) questionnaireColemanData.get("questionnaire");
        // Ajout de la personalisation
        String sexe = (String) questionnaireColemanData.getJSONObject("data").getJSONObject("EXTERNAL").get("TYPE_QUEST");
        List<JSONObject> personalisationContent = new ArrayList<>();
        // TODO : Remplacer le string partition par la variable de contexte partition
        switch (sexe) {
            case "1":
                JSONObject personalisationContent1 = new JSONObject();
                personalisationContent1.put("name", "whoAnswer1");
                personalisationContent1.put("value", "partition1.quiRepond1");

                JSONObject personalisationContent2 = new JSONObject();
                personalisationContent2.put("name", "whoAnswer2");
                personalisationContent2.put("value", "partition1.quiRepond2");

                JSONObject personalisationContent3 = new JSONObject();
                personalisationContent3.put("name", "whoAnswer1");
                personalisationContent3.put("value", "partition1.quiRepond2");

                personalisationContent.add(personalisationContent1);
                personalisationContent.add(personalisationContent2);
                personalisationContent.add(personalisationContent3);

                break;
            case "2":
                JSONObject personalisationContent21 = new JSONObject();
                personalisationContent21.put("name", "whoAnswer1");
                personalisationContent21.put("value", "partition2.quiRepond1");

                JSONObject personalisationContent22 = new JSONObject();
                personalisationContent22.put("name", "whoAnswer2");
                personalisationContent22.put("value", "partition2.quiRepond2");

                JSONObject personalisationContent23 = new JSONObject();
                personalisationContent23.put("name", "whoAnswer1");
                personalisationContent23.put("value", "partition2.quiRepond2");

                personalisationContent.add(personalisationContent21);
                personalisationContent.add(personalisationContent22);
                personalisationContent.add(personalisationContent23);
        }
        questionnaireColemanData.put("personalization", personalisationContent);
        return questionnaireColemanData;
    }
}
