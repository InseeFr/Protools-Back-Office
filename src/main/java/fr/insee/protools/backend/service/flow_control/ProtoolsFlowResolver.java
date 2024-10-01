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

    /**
     * Will check if the variable varname_is_last_page is set to true.
     * If yes it will unset variables varname_is_last_page & varname_current_page and return true
     *
     * @param execution
     * @param varname_current_page
     * @param varname_is_last_page
     * @return true if variable varname_is_last_page is true; else false
     */
    public boolean isPaginationFinished(ExecutionEntity execution, String varname_current_page, String varname_is_last_page) {
        Boolean isLastPage = FlowableVariableUtils.getVariableOrNull(execution, varname_is_last_page, Boolean.class);
        if (isLastPage != null && isLastPage) {
            Collection<String> variableNames = new ArrayList<>(2);
            variableNames.add(varname_current_page);
            variableNames.add(varname_is_last_page);
            execution.removeVariables(variableNames);
            return true;
        }
        return false;
    }

    public boolean isRemPaginationFinished(ExecutionEntity execution) {
        return isPaginationFinished(execution, VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE, VARNAME_INTERRO_LIST_PAGEABLE_IS_LAST_PAGE);
    }

}
