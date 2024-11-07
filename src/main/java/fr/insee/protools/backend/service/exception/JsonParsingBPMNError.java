package fr.insee.protools.backend.service.exception;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class JsonParsingBPMNError extends ProtoolsBpmnError {
    public JsonParsingBPMNError(String message, Throwable cause) {
        super(BPMNERROR_CODE_DEFAULT, message + " Exception: " + cause.getMessage());
    }
}
