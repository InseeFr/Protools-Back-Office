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
        List<JsonNode> contentList = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_INTERRO_LIST, List.class);
        Map<String,String> communicationRequestIdByInterroIdMap = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_COMMUNICATION_REQUEST_ID_FOR_INTERRO_ID_MAP, Map.class);

        if(currentCommunicationId.isBlank()){
            log.error("ProcessInstanceId={}  currentPartitionId={} - currentCommunicationId{} : currentCommunicationId cannot be blank",
                    execution.getProcessInstanceId(),currentPartitionId, currentCommunicationId);
            throw new ProtoolsProcessFlowBPMNError("PlatinePilotageCreateCommunicationEventTaskREST: communicationId cannot be empty");
        }

        //If nothing to do ==> Directly return
        if(contentList.isEmpty()){
            log.info("ProcessInstanceId={}  currentPartitionId={} - currentCommunicationId{} - end : Nothing to do",
                    execution.getProcessInstanceId(),currentPartitionId, currentCommunicationId);
            return;
        }


         //TODO: put it at a single place ==> Maybe an helper to get the Id of an interro or of any json?
        String jsonKeyId = "id";

        List<PlatinePilotageCommunicationEventDto> platinePilotageCommunicationEventList = contentList.stream()
                .filter(jsonNode -> {
                    JsonNode node = jsonNode.get(jsonKeyId);
                    if(node==null || node.isMissingNode()|| node.asText().isBlank()){
                        log.warn("ProcessInstanceId={}  currentPartitionId={}  : skipping an interro without id",execution.getProcessInstanceId(),currentPartitionId);
                        return false;
                    }
                    else if(!communicationRequestIdByInterroIdMap.containsKey(node.asText())){
                        log.warn("ProcessInstanceId={}  currentPartitionId={}  - interroID={} : cannot retrieve communicationRequestId",execution.getProcessInstanceId(),currentPartitionId,node.asText());
                        return false;
                    }
                    else{
                        return true;
                    }
                })
            .map(jsonNode -> {
                String interroId = jsonNode.path(jsonKeyId).asText();
                String communicationRequestId=communicationRequestIdByInterroIdMap.get(interroId);
                return PlatinePilotageCommunicationEventDto.builder()
                        .communcationId(currentCommunicationId)
                        .communicationRequestId(communicationRequestId)
                        .interrogationId(interroId).state(COMMUNICATION_STATE_SENT).build();
            }).toList();

        //Check ctx?
        platinePilotageService.postCommunicationEvent(platinePilotageCommunicationEventList);

        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());

    }
}