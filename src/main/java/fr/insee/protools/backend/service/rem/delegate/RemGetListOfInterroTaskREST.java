package fr.insee.protools.backend.service.rem.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.rem.IRemService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_ID_INTERRO_LIST;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_INTERRO_LIST;

@Slf4j
@Component
@Data
@RequiredArgsConstructor
public class RemGetListOfInterroTaskREST implements JavaDelegate, DelegateContextVerifier {

    private final IRemService remService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin", execution.getProcessInstanceId());
        List<String> idList = FlowableVariableUtils.getVariableOrNull(execution, VARNAME_ID_INTERRO_LIST, List.class);
        List<JsonNode> interrogations = remService.getLitOfInterro(idList);
        execution.getParent().setVariable(VARNAME_REM_INTERRO_LIST, interrogations);
        log.info("ProcessInstanceId={}  end", execution.getProcessInstanceId());
    }
}