package fr.insee.protools.backend.service.exception;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class IncorrectSUBPMNError extends ProtoolsBpmnError {
    private final String remSU;

    public IncorrectSUBPMNError(String message, JsonNode remSUNode, Exception e) {
        super(BPMNERROR_CODE_DEFAULT, message + " Exception : " + e.getMessage());
        if (remSUNode == null) {
            remSU = "null node";
        } else {
            this.remSU = remSUNode.toString();
        }
    }

    public IncorrectSUBPMNError(String message, JsonNode remSUNode) {
        super(BPMNERROR_CODE_DEFAULT, message);
        if (remSUNode == null) {
            remSU = "null node";
        } else {
            this.remSU = remSUNode.toString();
        }
    }

    public IncorrectSUBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
        remSU = "";
    }

    public String getRemSU() {
        return remSU;
    }
}