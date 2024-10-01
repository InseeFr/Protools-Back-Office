package fr.insee.protools.backend.service.flow_control;

import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_INTERRO_LIST_PAGEABLE_IS_LAST_PAGE;

/**
 * provide functions used in bpmn flow
 */
@Component
@RequiredArgsConstructor
public class ProtoolsFlowResolver {

    public boolean isPaginationFinished(ExecutionEntity execution, String varnameCurrentPage, String varnameIsLastPage) {
        Boolean isLastPage = FlowableVariableUtils.getVariableOrNull(execution, varnameIsLastPage, Boolean.class);
        if (isLastPage != null && isLastPage) {
            Collection<String> variableNames = new ArrayList<>(2);
            variableNames.add(varnameCurrentPage);
            variableNames.add(varnameIsLastPage);
            execution.removeVariables(variableNames);
            return true;
        }
        return false;
    }

    /**
     * Will check if VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE varname_is_last_page is set to true.
     * If yes it will unset variables VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE & VARNAME_INTERRO_LIST_PAGEABLE_IS_LAST_PAGE and return true
     *
     * @param execution
     * @return true if variable VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE is true; else false
     */
    public boolean isRemPaginationFinished(ExecutionEntity execution) {
        return isPaginationFinished(execution, VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE, VARNAME_INTERRO_LIST_PAGEABLE_IS_LAST_PAGE);
    }

}
