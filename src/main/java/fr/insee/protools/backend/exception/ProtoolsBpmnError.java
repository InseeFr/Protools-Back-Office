package fr.insee.protools.backend.exception;

import org.flowable.engine.delegate.BpmnError;


@SuppressWarnings("squid:S110")
public class ProtoolsBpmnError extends BpmnError {
    public ProtoolsBpmnError(String errorCode) {
        super(errorCode);
    }

    //Hack : currently we do not use the error code and use the message as both the errorcode and the message
    //Because we cannot get the message from within an eventSubprocess used to handle the bpmn error
    //OF course it is should be reverted when we find the solution
    public ProtoolsBpmnError(String errorCode, String message) {
        super(message, message);
    }
}
