package fr.insee.protools.backend.service.rem.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.rem.IRemService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_INTERRO_REMISE_EN_COLLECTE_LIST;

@Slf4j
@Component
@RequiredArgsConstructor
public class RemPostResmiseEnCollecteTaskREST implements JavaDelegate, DelegateContextVerifier {

    private final IRemService remService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={} begin", execution.getProcessInstanceId());

        //Get the contacts
        List<JsonNode> interroRemiseEnCollecteList = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_INTERRO_REMISE_EN_COLLECTE_LIST, List.class);
        //If nothing to do ==> Directly return
        if (interroRemiseEnCollecteList.isEmpty()) {
            log.info("ProcessInstanceId={} - end : Nothing to do",
                    execution.getProcessInstanceId());
            return;
        }

        remService.postRemiseEnCollecte(interroRemiseEnCollecteList);
        log.info("ProcessInstanceId={} end", execution.getProcessInstanceId());
    }

}