package com.protools.flowableDemo.services.era;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.protools.flowableDemo.helpers.client.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DrawDailySampleServiceTask implements JavaDelegate {
    @Value("${fr.insee.era.api}")
    private String eraUrl;

    @Value("${fr.insee.keycloak.realm.internal:#{null}}")
    private String realm;

    @Autowired WebClientHelper webClientHelper;

    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Draw Daily Sample Service Task <<  ");
        try {
            List<Map> listOfSampleUnit = getSampleIDs();
            delegateExecution.setVariable("sample",listOfSampleUnit);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

    // Get daily sample IDs from ERA
    public List<Map> getSampleIDs() throws ParseException, JsonProcessingException {
        /*Calendar cal = Calendar.getInstance();
        // Set hours, minutes, seconds and millis to zero to avoid errors
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -1);
        Date start = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cal.add(Calendar.DATE, +1);
        // Today's date
        Date end = cal.getTime();
        // Yesterday's date

        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-JJ");
        String startDate = sdf.format(start);
        String endDate = sdf.format(end);*/
        String startDate = "2022-01-24";
        String endDate = "2022-01-25";


        log.info("\t \t >> Get survey sample for today : {} << ", endDate.toString());

        var responseList =
            webClientHelper.getWebClientForRealm(realm,eraUrl).get()
                .uri(uriBuilder -> uriBuilder
                    .path("/extraction-survey-unit/survey-units-for-period")
                    .queryParam("startDate", startDate)
                    .queryParam("endDate", endDate)
                    .build())
                .retrieve()
                .bodyToMono(LinkedHashMap[].class)
                .block();

        log.info("\t \t >>> Got today's sample from ERA  : " + responseList.toString());
        return Arrays.asList(responseList);
    }
}
