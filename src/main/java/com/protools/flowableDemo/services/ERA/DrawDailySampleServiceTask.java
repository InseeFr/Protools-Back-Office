package com.protools.flowableDemo.services.ERA;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.swagger.models.auth.In;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DrawDailySampleServiceTask implements JavaDelegate {
    Logger logger = LoggerFactory.getLogger(DrawDailySampleServiceTask.class);

    @Value("${fr.insee.era.api}")
    private String eraUrl;
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        logger.info("\t >> Draw Daily Sample Service Task <<  ");
        try {
            List<Integer> listOfSampleIds = getSampleIDs();
            delegateExecution.setVariable("sampleIds",listOfSampleIds);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    // Get daily sample IDs from ERA
    public List<Integer> getSampleIDs() throws ParseException, JsonProcessingException {
        Calendar cal = Calendar.getInstance();
        // Set hours, minutes, seconds and millis to zero to avoid errors
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Today's date
        Date end = cal.getTime();
        // Yesterday's date
        cal.add(Calendar.DATE, -1);
        Date start = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-JJ");
        String startDate = sdf.format(start);
        String endDate = sdf.format(end);

        logger.info("\t \t >> Get survey sample for today : {} << ", endDate.toString());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(eraUrl+"/extraction-survey-unit/survey-units-for-period?startDate="+startDate+"&endDate="+endDate))
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

        Gson gson = new Gson();
        List<String> responseList = (List<String>) gson.fromJson(gson.toJson(response.body()),List.class);
        List<Integer> listOfIds = new ArrayList<>();
        for (String s : responseList) {
            logger.info("\t \t >> Sample ID : {} << ", s);
            Map unitMap = gson.fromJson(gson.toJson(s), Map.class);
            listOfIds.add((Integer) unitMap.get("id"));
        }
        logger.info("\t \t >>> Got today's sample from ERA  : " + listOfIds.toString());
        return listOfIds;
    }

}