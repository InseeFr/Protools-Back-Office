package fr.insee.protools.backend.service.logging;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;

@Component
@Slf4j
public class LogVariablesTask implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {

        log.info("ID={} - PATH_ID={} - Variables : {}", delegateExecution.getCurrentFlowElement().getId(), delegateExecution.getId(), filterVariables(delegateExecution.getVariables()));
        log.info("ID={} - PATH_ID={} - VariablesLocal : {}", delegateExecution.getCurrentFlowElement().getId(), delegateExecution.getId(), filterVariables(delegateExecution.getVariablesLocal()));
        if (delegateExecution.getParent() != null) {
            log.info("ID={} - PATH_ID={} - PARENT - Variables : {}", delegateExecution.getCurrentFlowElement().getId(), delegateExecution.getId(), filterVariables(delegateExecution.getParent().getVariables()));
            log.info("ID={} - PATH_ID={} - PARENT - VariablesLocal : {}", delegateExecution.getCurrentFlowElement().getId(), delegateExecution.getId(), filterVariables(delegateExecution.getParent().getVariablesLocal()));
        }
    }

    Map<String, Object> filterVariables(Map<String, Object> input) {
        return input.entrySet().stream()
                .filter(entry -> (!entry.getKey().equals(VARNAME_CONTEXT))
                        && (!entry.getKey().equals(VARNAME_REM_INTERRO_LIST))
                        && (!entry.getKey().equals(VARNAME_DIRECTORYACCESS_PWD_FOR_INTERRO_ID_MAP))
                )
                .collect(
                        Collectors.toMap(Map.Entry::getKey, entry -> (entry.getValue() == null) ? "null" : entry.getValue()));
    }
}
