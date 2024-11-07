package fr.insee.protools.backend.service.platine.delegate.questionnaire;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.service.context.IContextService;
import fr.insee.protools.backend.service.platine.service.IPlatineQuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlatineQuestionnaireCreateContextTaskREST implements JavaDelegate {

    private final IContextService protoolsContext;
    private final IPlatineQuestionnaireService platineQuestionnaireService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin", execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextJsonNodeByProcessInstance(execution.getProcessInstanceId());
        ContexteProcessus contexteProcessus = protoolsContext.getContextDtoByProcessInstance(execution.getProcessInstanceId());

        //No context used (only passed as json)
        platineQuestionnaireService.postContext(contexteProcessus.getId().toString(), contextRootNode);

        log.info("ProcessInstanceId={}  end", execution.getProcessInstanceId());

    }
}