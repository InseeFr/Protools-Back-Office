package com.protools.flowableDemo.services.messhugah;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Slf4j
public class SurveyUnitFollowUpService {

    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;

    public JSONObject checkIfUnitNeedsToBeFollowedUp(String idCampaign, String unitID) {
        log.info("\t \t >> Check If Unit Needs To Be Followed Up");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/campaigns/"+idCampaign+ "/survey-units/"+unitID+"/follow-up"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .GET()
                .build();
        HttpResponse<String> response = null;
        JSONObject jsonResponse = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            jsonResponse = new JSONObject(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }
}
