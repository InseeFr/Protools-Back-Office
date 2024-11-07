package fr.insee.protools.backend.controller;

import fr.insee.protools.backend.restclient.exception.runtime.HttpClient4xxBPMNError;
import fr.insee.protools.backend.restclient.exception.runtime.HttpClient5xxBPMNError;
import fr.insee.protools.backend.service.context.exception.BadContextIOException;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectBPMNError;
import fr.insee.protools.backend.service.context.exception.BadContextNotJSONBPMNError;
import fr.insee.protools.backend.service.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
public class ProtoolsProcessControllerAdvice {


    @ExceptionHandler(BadContextIOException.class)
    public ResponseEntity<BadContextIOException> exceptionContextIOHandler(/*final HttpServletRequest req,*/ final BadContextIOException exception) {
        log.error("exceptionContextIOHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadContextNotJSONBPMNError.class})
    public ResponseEntity<String> exceptionContextNotXMLHandler(/*final HttpServletRequest req,*/ final BadContextNotJSONBPMNError exception) {
        log.error("exceptionContextNotXMLHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({BadContextIncorrectBPMNError.class})
    public ResponseEntity<String> exceptionContextIncorrectHandler(/*final HttpServletRequest req,*/ final BadContextIncorrectBPMNError exception) {
        log.error("exceptionContextIncorrectHandler  : " + exception.getMessage());
        return new ResponseEntity<>("Error with provided context: " + exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TaskNotFoundException.class})
    public ResponseEntity<String> exeptionTaskNotFoundHandler(/*final HttpServletRequest req, */final TaskNotFoundException exception) {
        log.error("exeptionTaskNotFoundHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ProcessDefinitionNotFoundException.class})
    public ResponseEntity<String> exeptionProcessDefinitionNotFoundHandler(/*final HttpServletRequest req, */final ProcessDefinitionNotFoundException exception) {
        log.error("exeptionProcessDefinitionNotFoundHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({HttpClient4xxBPMNError.class})
    public ResponseEntity<String> exceptionWebClient4xxHandler(/*final HttpServletRequest req, */final HttpClient4xxBPMNError exception) {
        log.error("exceptionWebClient4xxHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), exception.getHttpStatusCodeError());
    }

    @ExceptionHandler({HttpClient5xxBPMNError.class})
    public ResponseEntity<String> exceptionWebClient5xxHandler(/*final HttpServletRequest req, */final HttpClient5xxBPMNError exception) {
        log.error("exceptionWebClient5xxHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({IncorrectSUBPMNError.class})
    public ResponseEntity<String> exceptionIncorrectSUHandler(/*final HttpServletRequest req, */final IncorrectSUBPMNError exception) {
        log.error("exceptionIncorrectSUHandler  : " + String.format("%s - remSU=[%s]", exception.getMessage(), exception.getRemSU()));
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({VariableClassCastException.class})
    public ResponseEntity<String> exceptionVariableClassCastHandler(/*final HttpServletRequest req, */final VariableClassCastException exception) {
        log.error("exceptionVariableClassCastHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ProtoolsTaskBPMNError.class})
    public ResponseEntity<String> exceptionProtoolsTaskHandler(/*final HttpServletRequest req, */final ProtoolsTaskBPMNError exception) {
        log.error("exceptionProtoolsTaskHandler  : " + exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
