package fr.insee.protools.backend.service.exception;

import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * This exception is thrown when you try to create a process instance with an unknown process definition
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class ProcessDefinitionNotFoundException extends FlowableObjectNotFoundException {
    public ProcessDefinitionNotFoundException(String processDefinitionKey) {
        super("No process definition found with id '" + processDefinitionKey + "'.", ProcessDefinition.class);
    }

}
