package com.protools.flowableDemo.services.protools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.protools.flowableDemo.services.engineService.WorkflowService;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SaveContext implements JavaDelegate {
    @Autowired
    private WorkflowService workflowService;
    Logger logger = LoggerFactory.getLogger(SaveContext.class);
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        logger.info("\t >> Save Context Service Task <<  ");
        String xmlFile = (String) delegateExecution.getVariable("contextRawFile");
        JsonNode node = parseXml(xmlFile);
        Map<String,Object> result = saveContext(node);
        logger.info("Final result: " + result);
        delegateExecution.setVariables(result);
        delegateExecution.removeVariable("contextRawFile");

    }
    public JsonNode parseXml(String xmlFile){
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = null;
        try {
            node = xmlMapper.readTree(xmlFile);
            logger.info("Xml file parsed into JsonNode: " + node);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return node;
    }
    public Map<String, Object> saveContext(JsonNode node){
        //TODO : Sauvegarder les objets dans la base de données

        //Récupération premier niveau et init de la clé
        String key = "Campaign";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.convertValue(node, new TypeReference<Map<String, Object>>(){});
        logger.info("result: " + result);
        Map<String, Object> newVariables = new HashMap<>();
        //Iterate over result map
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            if (entry.getValue() instanceof LinkedHashMap) {
                Map<String,Object> subMap = (Map<String, Object>) entry.getValue();
                for (Map.Entry<String,Object> subEntry : subMap.entrySet()){
                    logger.info(subEntry.getKey() + ": " + subEntry.getValue()+ " of type "+ subEntry.getValue().getClass());
                    if (newVariables.containsKey(subEntry.getKey())){
                        // C'est super sale, mais ça suffit pour le moment
                        newVariables.put(subEntry.getKey()+".1", newVariables.remove(subEntry.getKey()));
                        newVariables.put(subEntry.getKey()+".2",subEntry.getValue());
                    } else {
                        newVariables.put(subEntry.getKey(),subEntry.getValue());}
                }
            } else {
                newVariables.put(entry.getKey(),entry.getValue());
            }
        }
        result.putAll(newVariables);
        return newVariables;


    }
}
