package fr.insee.protools.backend.service.context.exception;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

/**
 * Runtime exception indicating an IOException while reading context
 */
public class BadContextIOException extends BadContexteBPMNError {

    public BadContextIOException(String message, Throwable cause) {
        super(BPMNERROR_CODE_DEFAULT, message);
    }
}
