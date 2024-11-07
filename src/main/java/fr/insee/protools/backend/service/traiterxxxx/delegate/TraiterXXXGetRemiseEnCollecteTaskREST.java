package fr.insee.protools.backend.service.traiterxxxx.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.traiterxxxx.ITraiterXXXService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CURRENT_PARTITION_ID;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_INTERRO_REMISE_EN_COLLECTE_LIST;

@Slf4j
@RequiredArgsConstructor
@Component
public class TraiterXXXGetRemiseEnCollecteTaskREST implements JavaDelegate, DelegateContextVerifier {
    private final ITraiterXXXService service;

    @Override
    public void execute(DelegateExecution execution) {
        String currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, String.class);
        log.info("ProcessInstanceId={} - currentPartitionId={} begin", execution.getProcessInstanceId(), currentPartitionId);

        List<JsonNode> interroRemiseEnCollecteList = service.getRemiseEnCollecteForPartition(currentPartitionId);
        execution.getParent().setVariableLocal(VARNAME_INTERRO_REMISE_EN_COLLECTE_LIST, interroRemiseEnCollecteList);

        log.info("ProcessInstanceId={} - currentPartitionId={} - end", execution.getProcessInstanceId(), currentPartitionId);
    }
}
