package fr.insee.protools.backend.service.meshuggah.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.service.context.IContextService;
import fr.insee.protools.backend.service.meshuggah.IMeshuggahService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MeshuggahSendOpeningCommunicationForInterrogationListTaskREST implements JavaDelegate {

    private final IMeshuggahService meshuggahService;
    private final IContextService protoolsContext;
    private final ObjectMapper objectMapper;

    @Override
    public void execute(DelegateExecution execution) {
        ContexteProcessus context = protoolsContext.getContextDtoByProcessInstance(execution.getProcessInstanceId());
        List<JsonNode> interroList = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_REM_INTERRO_LIST, List.class);
        String currentCommunicationId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_COMMUNICATION_ID, String.class);
        HashMap<String, String> pwdByInterroId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_DIRECTORYACCESS_PWD_FOR_INTERRO_ID_MAP, HashMap.class);

        log.info("ProcessInstanceId={} - currentCommunicationId={} - begin",
                execution.getProcessInstanceId(), currentCommunicationId);

        //TODO : Voir ce qu'il faut exactement faire
        interroList =
                interroList.stream()
                        .map(jsonNode -> {
                                    String id = jsonNode.path("interrogationId").asText(); //TODO: id
                                    if (pwdByInterroId.containsKey(id)) {
                                        log.error("Add pwd for meshuggah for interrogation.id={} :   {}", id, pwdByInterroId.get(id));
                                        ObjectNode updatedNode = objectMapper.createObjectNode();
                                        updatedNode.setAll((ObjectNode) jsonNode); // Copy existing fields
                                        updatedNode.put("pwd", pwdByInterroId.get(id));
                                        return updatedNode;
                                    }
                                    log.error("No pwd for meshuggah for interrogation.id={} ", id);
                                    return jsonNode;
                                }
                        ).toList();

        meshuggahService.postCommunicationRequest(String.valueOf(context.getId()), currentCommunicationId, interroList);

        log.info("ProcessInstanceId={} - currentCommunicationId={} - end",
                execution.getProcessInstanceId(), currentCommunicationId);
    }
}

