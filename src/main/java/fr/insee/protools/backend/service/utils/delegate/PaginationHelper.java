package fr.insee.protools.backend.service.utils.delegate;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import fr.insee.protools.backend.service.exception.PageableAPIBPMNError;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import org.flowable.engine.delegate.DelegateExecution;

import java.util.List;
import java.util.Map;


public interface PaginationHelper {

    org.slf4j.Logger getLogger();

    /**
     * Custom Treatment for the page
     *
     * @return a Modifiable MAP with variables to update on the process instance (so we can make a single setVariablesLocal in getAndTreat)
     */
    Map<String, Object> treatPage(DelegateExecution execution, List<JsonNode> contentList);

    /**
     * Default way to read elements with a paginated API.
     * getAndTreat It will read a page using IGetFromService.
     * For this  page it will call treatPage
     * variables in the map returned by treatPage() will be added to the local variables of the execution
     * It will update the varnameIsLastPage and varnameCurrentPage variables
     *
     * @param execution
     * @param varnameCurrentPage
     * @param varnameIsLastPage
     * @param getService         a functional interface to be used for the read
     * @param getServiceParams   the parameters for the functional interface call
     */
    default void getAndTreat(DelegateExecution execution, String varnameCurrentPage, String varnameIsLastPage, IGetFromService getService, Object... getServiceParams) {
        getLogger().info("ProcessInstanceId={}  begin", execution.getProcessInstanceId());

        Integer currentPage = FlowableVariableUtils.getVariableOrNull(execution, varnameCurrentPage, Integer.class);
        Boolean isLastPage = FlowableVariableUtils.getVariableOrNull(execution, varnameIsLastPage, Boolean.class);

        Integer expectedPage;
        if (currentPage == null) {
            expectedPage = 0;
        } else {
            expectedPage = currentPage + 1;
        }

        if (isLastPage == null) {
            isLastPage = Boolean.FALSE;
        }

        if (Boolean.TRUE.equals(isLastPage)) {
            getLogger().warn("ProcessInstanceId={} : last page already reached", execution.getProcessInstanceId());
        } else {

            PageResponse<JsonNode> pageResponse = getService.apply(expectedPage, getServiceParams);

            //Verify that we got the correct page with not null content
            if (!pageResponse.getCurrentPage().equals(expectedPage)) {
                throw new PageableAPIBPMNError(String.format("Error while reading interrogations from REM - expected page=%s got page=%s", expectedPage, pageResponse.getCurrentPage()));
            } else if (pageResponse.getContent() == null) {
                throw new PageableAPIBPMNError("Error while reading interrogations from REM - content is null");
            }

            //Treat the response
            Map<String, Object> variables = treatPage(execution, pageResponse.getContent());

            //Add pagination details to the variables we are going to insert
            variables.put(varnameCurrentPage, expectedPage);
            variables.put(varnameIsLastPage, pageResponse.isLastPage());
            execution.getParent().setVariablesLocal(variables);
            getLogger().debug("ProcessInstanceId={} -  readSize={} end", execution.getProcessInstanceId(), pageResponse.getContent().size());
        }
    }

    @FunctionalInterface
    interface IGetFromService<R> {
        PageResponse apply(Integer pageToRead, Object... params);
    }
}
