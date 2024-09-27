package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.repository.IUniteEnquetee;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.IContextService;
import fr.insee.protools.backend.service.platine.service.IPlatineQuestionnaireService;
import fr.insee.protools.backend.service.scheduled.TaskService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_INTERRO_LIST;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlatineQuestionnaireCreateSurveyUnitTaskAsync implements JavaDelegate, DelegateContextVerifier {

    private final IContextService protoolsContext;
    private final IPlatineQuestionnaireService platineQuestionnaireService;
    private final IUniteEnquetee iUniteEnquetee;
    @Autowired
    TaskService taskService;

    @Override
    public void execute(DelegateExecution execution) {
        Boolean start = Boolean.FALSE;
        ContexteProcessus context = protoolsContext.getContextDtoByProcessInstance(execution.getProcessInstanceId());
        //TODO: delete this log if necessary
        log.debug("ProcessInstanceId={}  - campagne={} - begin"
                ,execution.getProcessInstanceId(),context.getId());

        checkContextOrThrow(log,execution.getProcessInstanceId(), context);

//        QuestionnaireHelper.createSUTaskPlatineAsync(execution,protoolsContext,iUniteEnquetee,platineQuestionnaireService);

        List<JsonNode> listeUe =   FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_INTERRO_LIST, List.class);
        String processInstanceId = execution.getProcessInstanceId();
        String currentActivityId = execution.getCurrentActivityId();
        iUniteEnquetee.addManyUniteEnquetee(listeUe, processInstanceId, currentActivityId);

//        iUniteEnquetee.addManyUniteEnqueteeDeleteColonneClass(listeUe);

//        TODO gestion des timeouts
        while (!start) {
            log.info("start : "+ start);
            try {
                long nbInterogation = iUniteEnquetee.getCommandesByProcessInstanceIdAndCurrentActivityId(execution.getProcessInstanceId(), execution.getCurrentActivityId());
                start = taskService.isTerminated(execution.getProcessInstanceId(), execution.getCurrentActivityId(), nbInterogation);
                log.info("Tâche planifiée en cours pour le processInstanceId : " + execution.getProcessInstanceId() + ", le currentActivityId " + execution.getCurrentActivityId() + ",et le nbInterogation totale " + nbInterogation);
                Thread.sleep(5000);
                if (start) {
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.debug("ProcessInstanceId={}  - campagne={} - end",
                execution.getProcessInstanceId(),context.getId());
    }
}
