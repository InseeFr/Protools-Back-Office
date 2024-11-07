package fr.insee.protools.backend.service.rem.delegate;

import fr.insee.protools.backend.restclient.pagination.PageResponse;
import fr.insee.protools.backend.service.rem.RemServiceImpl;
import fr.insee.protools.backend.service.utils.delegate.IDelegateWithVariableGetPaginated;
import org.flowable.engine.delegate.JavaDelegate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_PLATINE_CONTACT_LIST;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RemGetPartitionListOfInterroPaginatedTaskTest implements IDelegateWithVariableGetPaginated {

    @Mock
    RemServiceImpl remService;
    @InjectMocks
    RemGetPartitionListOfInterroPaginatedTaskREST task;

    @Override
    public JavaDelegate getTaskUnderTest() {
        return task;
    }

    @Override
    public Map<String, Class> getVariablesAndTypes() {
        return Map.of(
                VARNAME_CURRENT_PARTITION_ID, String.class
        );
    }

    @Override
    public void initReadValueMock(PageResponse expectedPageResponse) {
        lenient().doReturn(expectedPageResponse).when(remService).getPartitionAllInterroPaginated(anyString(), anyLong());
    }

    @Override
    public List<String> getOutListsVariableName() {
        return List.of(VARNAME_REM_INTERRO_LIST);
    }

    @Override
    public String getVarnameCurrentPage() {
        return VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE;
    }

    @Override
    public String getVarnameisLastPage() {
        return VARNAME_INTERRO_LIST_PAGEABLE_IS_LAST_PAGE;
    }

}