package fr.insee.protools.backend.service.platine.delegate.pilotatage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.dto.ContactInterrogation;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.platine.service.IPlatinePilotageService;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import fr.insee.protools.backend.service.utils.delegate.PaginationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class PlatinePilotageGetListOfInterroToFollowUpPaginatedTaskREST implements JavaDelegate, DelegateContextVerifier, PaginationHelper {

    private final IPlatinePilotageService pilotageService;
    private final ObjectMapper objectMapper;

    protected PageResponse readFunction(Integer pageToRead, Object... objects) {
        String partitionId = (String) objects[0];
        return pilotageService.getInterrogationToFollowUpPaginated(partitionId, pageToRead, Optional.of(Boolean.TRUE));
    }

    @Override
    public void execute(DelegateExecution execution) {
        String currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution, VARNAME_CURRENT_PARTITION_ID, String.class);
        getAndTreat(execution, VARNAME_FOLLOW_UP_LIST_PAGEABLE_CURRENT_PAGE, VARNAME_FOLLOW_UP_LIST_PAGEABLE_IS_LAST_PAGE, this::readFunction, currentPartitionId);
    }

    @Override
    public Logger getLogger() {
        return log;
    }

    @Override
    public Map<String, Object> treatPage(DelegateExecution execution, List<JsonNode> contentList) {
        Map<String, Object> variables = new HashMap<>();
        List<String> interrogationsToFollowUpList = new ArrayList<>(contentList.size());
        List<JsonNode> platineContactList = new ArrayList<>(contentList.size());

        contentList.forEach(jsonNode -> {
            ContactInterrogation contactInterrogation = objectMapper.convertValue(jsonNode, ContactInterrogation.class);
            interrogationsToFollowUpList.add(contactInterrogation.getInterrogationId());
            platineContactList.add(objectMapper.valueToTree(contactInterrogation.getContactsPlatine()));
        });

        variables.put(VARNAME_ID_INTERRO_LIST, interrogationsToFollowUpList);
        variables.put(VARNAME_PLATINE_CONTACT_LIST, platineContactList);

        return variables;
    }
}