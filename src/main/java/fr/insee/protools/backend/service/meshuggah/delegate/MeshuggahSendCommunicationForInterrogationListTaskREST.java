package fr.insee.protools.backend.service.meshuggah.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.dto.MeshuggahCommunicationRequestReponse;
import fr.insee.protools.backend.dto.internal.CommunicationRequestDetails;
import fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageCommunicationEventType;
import fr.insee.protools.backend.service.context.IContextService;
import fr.insee.protools.backend.service.meshuggah.IMeshuggahService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeshuggahSendCommunicationForInterrogationListTaskREST implements JavaDelegate {

    private final IMeshuggahService meshuggahService;
    private final IContextService protoolsContext;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        ContexteProcessus context = protoolsContext.getContextDtoByProcessInstance(execution.getProcessInstanceId());
        List<JsonNode> interroList = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_INTERRO_LIST, List.class);
        String currentCommunicationId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_COMMUNICATION_ID, String.class);

        log.info("ProcessInstanceId={} - currentCommunicationId={} - begin",
                execution.getProcessInstanceId(), currentCommunicationId);

        List<MeshuggahCommunicationRequestReponse> meshuggahComReqInterro = meshuggahService.postCommunicationRequest(String.valueOf(context.getId()), currentCommunicationId, interroList);
        //Store the information that the communication was correctly sent for this interrogation
        //Maybe in furture we will handle cases of failed communications
        Map<String, CommunicationRequestDetails> communicationStatusByInterroIdMap =
                meshuggahComReqInterro.stream()
                        ///Get the interrogation Id and filter incorrect interrogations
                        .map(response -> {
                            if (response.getInterrogationId() != null && response.getCommunicationRequestId() != null)
                                return response;
                            else {
                                log.warn("one of the interrogations was incorrect (no interrogationId and/or no communicationRequestId)");
                                return null;
                            }
                        })
                        .filter(i -> i != null)
                        .collect(Collectors.toMap(
                                MeshuggahCommunicationRequestReponse::getInterrogationId, //Key : interrogationId
                                response -> new CommunicationRequestDetails(response.getCommunicationRequestId(), PlatinePilotageCommunicationEventType.COMMUNICATION_STATE_SENT) //Value :
                        ));
        execution.getParent().setVariableLocal(VARNAME_COMMUNICATION_REQUEST_ID_AND_STATUS_FOR_INTERRO_ID_MAP, communicationStatusByInterroIdMap);
        log.info("ProcessInstanceId={} - currentCommunicationId={} - end",
                execution.getProcessInstanceId(), currentCommunicationId);
    }
}

