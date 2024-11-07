package fr.insee.protools.backend.service.platine.delegate.pilotatage;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.dto.ContactInterrogation;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import fr.insee.protools.backend.service.platine.service.PlatinePilotageServiceImpl;
import fr.insee.protools.backend.service.utils.delegate.IDelegateWithVariableGetPaginated;
import fr.insee.protools.backend.service.utils.delegate.IDelegateWithVariables;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatinePilotageGetListOfInterroToFollowUpPaginatedTaskRESTTest
        implements IDelegateWithVariableGetPaginated {

    @Spy
    ObjectMapper objectMapper;

    @Mock
    PlatinePilotageServiceImpl platinePilotageService;
    @InjectMocks
    PlatinePilotageGetListOfInterroToFollowUpPaginatedTaskREST task;

    @Override
    public JavaDelegate getTaskUnderTest() {
        return task;
    }

    @Override
    public void initReadValueMock(PageResponse expectedPageResponse) {
        lenient().doReturn(expectedPageResponse).when(platinePilotageService).getInterrogationToFollowUpPaginated(anyString(), anyLong(),any());
    }

    @Override
    public List getDefaultContent() {
        ContactInterrogation contactInterrogation = new ContactInterrogation();
        contactInterrogation.setContactsPlatine(List.of());
        contactInterrogation.setInterrogationId("id1");
        return List.of(objectMapper.valueToTree(contactInterrogation));
    }

    @Override
    public Map<String, Class> getVariablesAndTypes() {
        return Map.of(
                VARNAME_CURRENT_PARTITION_ID, String.class
        );
    }

    @Override
    public List<String> getOutListsVariableName() {
        return List.of(VARNAME_ID_INTERRO_LIST,VARNAME_PLATINE_CONTACT_LIST);
    }

    @Override
    public String getVarnameCurrentPage() {
        return VARNAME_FOLLOW_UP_LIST_PAGEABLE_CURRENT_PAGE;
    }

    @Override
    public String getVarnameisLastPage() {
        return VARNAME_FOLLOW_UP_LIST_PAGEABLE_IS_LAST_PAGE;
    }
}