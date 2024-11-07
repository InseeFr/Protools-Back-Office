package fr.insee.protools.backend.restclient.exception.runtime;

import fr.insee.protools.backend.exception.ProtoolsBpmnError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import static fr.insee.protools.backend.service.BPMNErrorCode.BPMNERROR_CODE_DEFAULT;

@Slf4j
public class HttpClientRequestExceptionBPMNError extends ProtoolsBpmnError {

    private final WebClientRequestException ex;

    public HttpClientRequestExceptionBPMNError(WebClientRequestException ex) {
        super(BPMNERROR_CODE_DEFAULT, computeMessage(ex));
        this.ex = ex;
        log.error(this.getMessage());
    }

    private static String computeMessage(WebClientRequestException ex) {
        return String.format("WebClientException : class=[%s] - method=[%s] - uri=[%s] message=[%s] - root_cause=[%s] - root_message=[%s]",
                ex.getClass(),
                ex.getMethod(),
                ex.getUri(),
                ex.getMessage(),
                ex.getMostSpecificCause().getClass(),
                ex.getMostSpecificCause().getMessage());
    }


    public WebClientRequestException getBaseException() {
        return ex;
    }

}
