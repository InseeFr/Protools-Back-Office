package fr.insee.protools.backend.service.utils.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CURRENT_PARTITION_ID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public interface IDelegateWithVariableGetPaginated extends IDelegateWithVariables {

    ObjectMapper objectMapper = new ObjectMapper();

    void initReadValueMock(PageResponse pageResponse);
    List<String> getOutListsVariableName();

    String getVarnameCurrentPage();
    String getVarnameisLastPage();

    @Override
    default void initExtraMocks(DelegateExecution execution) {
        IDelegateWithVariables.super.initExtraMocks(execution);
        PageResponse exepectedPageResponse = PageResponse.builder().currentPage(0).pageCount(1).content(getDefaultContent()).build();
        initReadValueMock(exepectedPageResponse);
    }

    default List getDefaultContent(){
        return List.of(objectMapper.createObjectNode().put("xx","yyy"));
    }

    static Stream<Arguments> executeParamProvider() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of(null, false),
                Arguments.of(0, true),
                Arguments.of(0, false),
                Arguments.of(1, true),
                Arguments.of(1, false),
                Arguments.of(99, true),
                Arguments.of(1011, false)
        );
    }

    @ParameterizedTest
    @MethodSource("executeParamProvider")
    default void execute_should_work_when_params_notNullWithPaginated(Integer currentPage,boolean isLastPage) {
        //Preconditions
        DelegateExecution execution = mock(DelegateExecution.class);
        lenient().when(execution.getProcessInstanceId()).thenReturn(dumyId);
        lenient().when(execution.getParent()).thenReturn(execution);

        initDefaultVariables(execution);

        //Pages data
        lenient().doReturn(currentPage).when(execution).getVariable(eq(getVarnameCurrentPage()), eq(Integer.class));
        lenient().doReturn(currentPage).when(execution).getVariableLocal(eq(getVarnameCurrentPage()), eq(Integer.class));

        lenient().doReturn(isLastPage).when(execution).getVariable(eq(getVarnameisLastPage()), eq(Boolean.class));
        lenient().doReturn(isLastPage).when(execution).getVariableLocal(eq(getVarnameisLastPage()), eq(Boolean.class));


        //PageResponse
        Integer expectedPageToRead=(currentPage==null)?0:currentPage+1;
        Integer expectedPageCount=(isLastPage)?expectedPageToRead+1:expectedPageToRead+10;
        PageResponse exepectedPageResponse = PageResponse.builder().currentPage(expectedPageToRead).pageCount(expectedPageCount).content(getDefaultContent()).build();
        initReadValueMock(exepectedPageResponse);
        //Call method under test
        getTaskUnderTest().execute(execution);

        if(!isLastPage) {
            // Verify the flowable utils are called to get and treat variables
            verify(execution, times(1)).getVariable(VARNAME_CURRENT_PARTITION_ID, String.class);

            ArgumentCaptor<Map<String, Object>> variablesMapCaptor = ArgumentCaptor.forClass(Map.class);
            verify(execution).setVariablesLocal(variablesMapCaptor.capture());
            Map<String, Object> capturedMap = variablesMapCaptor.getValue();

            for(String expectedOutList: getOutListsVariableName()){
                assertTrue(capturedMap.containsKey(expectedOutList),"Expected out list is has not been set");
            }
        }
    }
}
