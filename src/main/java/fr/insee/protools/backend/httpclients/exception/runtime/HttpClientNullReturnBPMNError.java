package fr.insee.protools.backend.httpclients.exception.runtime;


import fr.insee.protools.backend.exception.ProtoolsBpmnError;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

public class HttpClientNullReturnBPMNError extends ProtoolsBpmnError {
    public HttpClientNullReturnBPMNError(String message) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}