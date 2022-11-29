package com.protools.flowableDemo.services.messhugah;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protools.flowableDemo.keycloak.KeycloakService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SendFollowUpMailServiceTask implements JavaDelegate {


    @Autowired
    private SendMailService sendMailService;
    @Override
    public void execute(org.flowable.engine.delegate.DelegateExecution delegateExecution) {
        log.info("\t >> Send Follow Up Email ServiceTask");

        // Retrieve email content data
        Map unit = (Map) delegateExecution.getVariable("unit");
        int sexe = Integer.valueOf((String) unit.get("sexe"));
        List<LinkedHashMap<String,Object>> partition = (List<LinkedHashMap<String,Object>>) delegateExecution.getVariable("Partition");
        LinkedHashMap<String,Object> communications = (LinkedHashMap<String,Object>) partition.get(sexe - 1).get("Communications");
        List<LinkedHashMap<String,Object>> communication = (List<LinkedHashMap<String, Object>>) communications.get("Communication");

        //TODO : Faire moins degueu
        LinkedHashMap<String,Object> communicationRelance = null;
        LinkedHashMap<String,Object> contenuCommunication = null;
        for (LinkedHashMap<String,Object> comm: communication ){
            //log.info("Comm: " + comm.toString());
            //log.info("Comm type: " + comm.get("MoyenCommunication")+" - "+ comm.get("TypeCommunication"));
            if (comm.get("MoyenCommunication").equals("mail") && comm.get("TypeCommunication").equals("relance")){

                communicationRelance = comm;
                contenuCommunication = (LinkedHashMap<String,Object>) comm.get("ContenuCommunication");

                // Retrieve Campaign data
                // This part is not mandatory, it only serves as a mark to not get lost in all those variables
                String Enq_ServiceCollecteurSignataireFonction = (String) delegateExecution.getVariable("Enq_ServiceCollecteurSignataireFonction");
                String Enq_ServiceCollecteurSignataireNom = (String) delegateExecution.getVariable("Enq_ServiceCollecteurSignataireNom");
                String Enq_RespTraitement = (String) delegateExecution.getVariable("Enq_RespTraitement");
                String Enq_RespOperationnel = (String) delegateExecution.getVariable("Enq_RespOperationnel");
                String Enq_MailRespOperationnel = (String) delegateExecution.getVariable("Enq_MailRespOperationnel");
                String Enq_UrlEnquete = (String) delegateExecution.getVariable("Enq_UrlEnquete");
                String Enq_LogoPrestataire = (String) delegateExecution.getVariable("Enq_LogoPrestataire");
                String Enq_Prestataire = (String) delegateExecution.getVariable("Enq_Prestataire");

                // Create Email request body
                LinkedHashMap<String,Object> finalContenuCommunication = contenuCommunication;
                LinkedHashMap<String,Object> finalCommunicationRelance = communicationRelance;
                var data = new HashMap<String, Object>() {{
                    put("Ue_CalcIdentifiant", unit.get("internaute"));
                    put("Enq_ThemeMieuxConnaitreMail", finalContenuCommunication.get("ThemeMieuxConnaitreMail"));
                    put("Enq_ServiceCollecteurSignataireFonction", Enq_ServiceCollecteurSignataireFonction);
                    put("Enq_ServiceCollecteurSignataireNom", Enq_ServiceCollecteurSignataireNom);
                    put("Enq_RespTraitement", Enq_RespTraitement);
                    put("Enq_RespOperationnel",Enq_RespOperationnel);
                    put("Enq_MailRespOperationnel",Enq_MailRespOperationnel);
                    put("Enq_UrlEnquete",Enq_UrlEnquete);
                    put("Enq_LogoPrestataire",Enq_LogoPrestataire);
                    put("Enq_Prestataire",Enq_Prestataire);
                    put("Mail_Objet", finalCommunicationRelance.get("Objet"));
                    put("Mail_BoiteRetour", finalCommunicationRelance.get("BoiteRetour"));
                    put("Enq_RelanceLibreMailParagraphe1", finalContenuCommunication.get("RelanceLibreMailParagraphe1"));
                    put("Enq_RelanceLibreMailParagraphe2", finalContenuCommunication.get("RelanceLibreMailParagraphe2"));
                    put("Enq_RelanceLibreMailParagraphe3", finalContenuCommunication.get("RelanceLibreMailParagraphe3"));
                    put("Enq_RelanceLibreMailParagraphe4", finalContenuCommunication.get("RelanceLibreMailParagraphe4"));
                    put("Enq_ComplementConnexion", finalContenuCommunication.get("ComplementConnexion"));
                }};
                var values = new HashMap<String, Object>() {{
                    put("email", unit.get("mail"));
                    put("data", data);

                }};
                log.info("\t \t FollowUp Mail data: " + values);
                var objectMapper = new ObjectMapper();
                String requestBody = null;
                try {
                    requestBody = objectMapper
                            .writeValueAsString(values);
                    sendMailService.SendMail(requestBody);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }
}
