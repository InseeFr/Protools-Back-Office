package fr.insee.protools.backend.service.sugoi.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
import org.flowable.variable.service.HistoricVariableService;
import org.flowable.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_DIRECTORYACCESS_PWD_CONTACT;

/**
 * Supprime la variable mot de passe et supprime aussi toutes les occurences de l'historique
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CleanUserPasswordTask implements JavaDelegate {

    private final ProcessEngineConfigurationImpl processEngineConfiguration;

    @Override
    public void execute(DelegateExecution execution) {
        log.debug("ProcessInstanceId={} begin", execution.getProcessInstanceId());

        //Supprime la variable mot de passe si elle existe
        execution.removeVariable(VARNAME_DIRECTORYACCESS_PWD_CONTACT);

        //La supprime aussi de l'historique
        HistoricVariableService historicVariableService = processEngineConfiguration.getVariableServiceConfiguration().getHistoricVariableService();
        HistoryService historyService = processEngineConfiguration.getHistoryService();

        HistoricVariableInstanceQuery query = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(execution.getProcessInstanceId())
                .variableName(VARNAME_DIRECTORYACCESS_PWD_CONTACT);

        List<HistoricVariableInstance> variableInstances = query.list();
        for (HistoricVariableInstance variableInstance : variableInstances) {
            historicVariableService.deleteHistoricVariableInstance((HistoricVariableInstanceEntity) variableInstance);
        }
    }
}
