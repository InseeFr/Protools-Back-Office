package fr.insee.protools.backend.service.platine.delegate.pilotatage;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageCommunicationEventDto;
import fr.insee.protools.backend.exception.ProtoolsProcessFlowBPMNError;
import fr.insee.protools.backend.service.platine.service.PlatinePilotageService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageCommunicationEventType.COMMUNICATION_STATE_SENT;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class PlatinePilotageCreateCommunicationEventTaskREST implements JavaDelegate {

    private final PlatinePilotageService platinePilotageService;

    @Override
    public void execute(DelegateExecution execution) {
        String currentCommunicationId= FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_COMMUNICATION_ID, String.class);
        String currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, String.class);

        log.info("ProcessInstanceId={}  currentPartitionId={} - currentCommunicationId{} - begin",
                execution.getProcessInstanceId(),currentPartitionId, currentCommunicationId);
        Map<String,String> communicationRequestIdByInterroIdMap = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_COMMUNICATION_REQUEST_ID_FOR_INTERRO_ID_MAP, Map.class);

        if(currentCommunicationId.isBlank()){
            log.error("ProcessInstanceId={}  currentPartitionId={} - currentCommunicationId{} : currentCommunicationId cannot be blank",
                    execution.getProcessInstanceId(),currentPartitionId, currentCommunicationId);
            throw new ProtoolsProcessFlowBPMNError("PlatinePilotageCreateCommunicationEventTaskREST: communicationId cannot be empty");
        }

        //If nothing to do ==> Directly return
        if(communicationRequestIdByInterroIdMap.isEmpty()){
            log.info("ProcessInstanceId={}  currentPartitionId={} - currentCommunicationId{} - end : Nothing to do",
                    execution.getProcessInstanceId(),currentPartitionId, currentCommunicationId);
            return;
        }

        List<PlatinePilotageCommunicationEventDto> platinePilotageCommunicationEventList = communicationRequestIdByInterroIdMap.entrySet()
                .stream()
                .map(entry ->
                        PlatinePilotageCommunicationEventDto.builder()
                                .communcationId(currentCommunicationId)
                                .communicationRequestId(entry.getValue())
                                .interrogationId(entry.getKey()).state(COMMUNICATION_STATE_SENT).build())
                .toList();

        //Check ctx?
        platinePilotageService.postCommunicationEvents(platinePilotageCommunicationEventList);

        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());

    }
}