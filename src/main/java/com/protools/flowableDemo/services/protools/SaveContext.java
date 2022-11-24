package com.protools.flowableDemo.services.protools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.protools.flowableDemo.services.engineService.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SaveContext implements JavaDelegate {
    @Autowired
    private WorkflowService workflowService;
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Save Context Service Task <<  ");
        String xmlFile = (String) delegateExecution.getVariable("contextRawFile");
        JsonNode node = parseXml(xmlFile);
        Map<String,Object> result = saveContext(node);
        delegateExecution.setVariables(result);

        // Extraction données du timer de cloture de campagne
        // TODO : à déplacer dans une fonction dédiée
        try {
            List<Object> partitionsList = (List<Object>) delegateExecution.getVariable("Partition");
            LinkedHashMap<Object,Object> partitionsStr = (LinkedHashMap<Object, Object>) partitionsList.get(0);
            String dateFinCampagne = getDateFinCampagne(partitionsStr);
            delegateExecution.setVariable("dateFinCampagne",dateFinCampagne);
        } catch (Exception e) {
            log.info("Could not extract dateFinCampagne from context file, error: " + e.getMessage());
        }


        // Purge de la variable initiale
        delegateExecution.removeVariable("contextRawFile");

    }
    public JsonNode parseXml(String xmlFile){
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = null;
        try {
            node = xmlMapper.readTree(xmlFile);
            log.info("Xml file parsed into JsonNode: " + node);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return node;
    }
    public Map<String, Object> saveContext(JsonNode node){
        //Récupération premier niveau et init de la clé
        String key = "Campaign";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.convertValue(node, new TypeReference<Map<String, Object>>(){});
        //log.info("result: " + result);
        Map<String, Object> newVariables = new HashMap<>();
        //Iterate over result map
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            if (entry.getValue() instanceof LinkedHashMap) {
                Map<String,Object> subMap = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String,Object> subEntry : subMap.entrySet()){
                    //log.info(subEntry.getKey() + ": " + subEntry.getValue()+ " of type "+ subEntry.getValue().getClass());
                    newVariables.put(subEntry.getKey(),subEntry.getValue());
                }
            } else {
                newVariables.put(entry.getKey(),entry.getValue());
            }
        }
        result.putAll(newVariables);
        return newVariables;


    }

    public String getDateFinCampagne(LinkedHashMap<Object,Object> partitionsStr){
        Gson gson = new Gson();
        Map<String, Object> partitions = gson.fromJson(gson.toJson(partitionsStr),Map.class);

        String dateFinCampagne = "2000-01-01";

        Map<String, Object> dateObject = (Map<String, Object>) partitions.get("Dates");
        dateFinCampagne = ((String)dateObject.get("CollectionEndDate")).substring(0, 10);

        log.info("Date de fin de campagne: " + dateFinCampagne);
        return dateFinCampagne;
    }
}
