package fr.insee.protools.backend.service.platine.delegate.pilotatage;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.service.context.IContextService;
import fr.insee.protools.backend.service.platine.service.IPlatinePilotageService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CURRENT_PARTITION_ID;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_INTERRO_LIST;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlatinePilotageCreateInterrogationListTaskREST implements JavaDelegate {

    private final IPlatinePilotageService platinePilotageService;
    private final IContextService protoolsContext;

    @Override
    public void execute(DelegateExecution execution) {
        ContexteProcessus context = protoolsContext.getContextDtoByProcessInstance(execution.getProcessInstanceId());
        String currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, String.class);
        List<JsonNode> interroList = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_INTERRO_LIST, List.class);

        log.info("ProcessInstanceId={} - currentPartitionId={} - begin", execution.getProcessInstanceId(), currentPartitionId);
        platinePilotageService.postInterrogations(String.valueOf(context.getId()), interroList);
        log.info("ProcessInstanceId={} - currentPartitionId={} - end", execution.getProcessInstanceId(), currentPartitionId);
    }
}
