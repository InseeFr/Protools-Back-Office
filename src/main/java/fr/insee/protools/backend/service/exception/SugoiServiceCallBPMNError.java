package fr.insee.protools.backend.service.exception;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class SugoiServiceCallBPMNError extends ProtoolsBpmnError {

    public SugoiServiceCallBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
