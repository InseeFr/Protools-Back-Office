package com.protools.flowableDemo.services.messhugah;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;


@Component
@Slf4j
class SendMailService {
    @Value("${fr.insee.coleman.pilotage.uri}")
    private String colemanPilotageUri;
    //TODO : Check si url reste la même
    public void SendMail(JSONObject mailContent){
        log.info("\t \t >> Send Mail Task ");

        HttpClient client = HttpClient.newHttpClient();
        var objectMapper = new ObjectMapper();
        String mailContentPilotage = null;
        try {
            mailContentPilotage = objectMapper
                    .writeValueAsString(mailContent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(colemanPilotageUri+"/contact/send-mail"))
                .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(Objects.requireNonNull(mailContentPilotage)))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("\t \t Mail data sent to Messhugah with response status code : "+ response.statusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
