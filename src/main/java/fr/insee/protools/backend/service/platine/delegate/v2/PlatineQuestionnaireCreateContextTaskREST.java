package fr.insee.protools.backend.service.platine.delegate.v2;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.platine.pilotage.PlatinePilotageService;
import fr.insee.protools.backend.service.platine.questionnaire.PlatineQuestionnaireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import static fr.insee.protools.backend.service.context.ContextConstants.CTX_CAMPAGNE_ID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlatineQuestionnaireCreateContextTaskREST implements JavaDelegate, DelegateContextVerifier {

    private final ContextService protoolsContext;
    private final PlatineQuestionnaireService platineQuestionnaireService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin",execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());
        String campainId = contextRootNode.path(CTX_CAMPAGNE_ID).asText();

        checkContextOrThrow(log,execution.getProcessInstanceId(), contextRootNode);
        platineQuestionnaireService.postContext(campainId,contextRootNode);

        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());

    }
}