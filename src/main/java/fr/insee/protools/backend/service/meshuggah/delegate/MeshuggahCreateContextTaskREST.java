package fr.insee.protools.backend.service.meshuggah.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.IContextService;
import fr.insee.protools.backend.service.meshuggah.IMeshuggahService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class MeshuggahCreateContextTaskREST implements JavaDelegate, DelegateContextVerifier {
    private final IContextService protoolsContext;
    private final IMeshuggahService service;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin", execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextJsonNodeByProcessInstance(execution.getProcessInstanceId());
        ContexteProcessus contexteProcessus = protoolsContext.getContextDtoByProcessInstance(execution.getProcessInstanceId());

        checkContextOrThrow(log, execution.getProcessInstanceId(), contexteProcessus);
        String campainId = contexteProcessus.getId().toString();

        service.postContext(campainId, contextRootNode);

        log.info("ProcessInstanceId={}  end", execution.getProcessInstanceId());
    }
}
