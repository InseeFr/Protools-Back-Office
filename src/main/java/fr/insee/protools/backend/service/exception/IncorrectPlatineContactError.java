package fr.insee.protools.backend.service.exception;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class IncorrectPlatineContactError extends ProtoolsBpmnError {
    private final String contact;

    public IncorrectPlatineContactError(String message, JsonNode contact, Exception e) {
        super(BPMNERROR_CODE_DEFAULT, message + " Exception : " + e.getMessage());
        if (contact == null) {
            this.contact = "null node";
        } else {
            this.contact = contact.toString();
        }
    }

    public String getContact() {
        return contact;
    }
}