package fr.insee.protools.backend.service.context;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.service.context.exception.BadContexMissingBPMNError;
import org.springframework.web.multipart.MultipartFile;

public interface IContextService {
    /**
     * Process the uploaded file and store it's content in VARIABLE_NAME_SERIALIZED_CONTEXT_FILE engine variable.
     * If ok pass the task referenced by *taskId* to completed
     *
     * @param file
     * @param taskId
     */
    void processContextFileAndCompleteTask(MultipartFile file, String taskId);

    /**
     * Process the uploaded file and store it's content in VARIABLE_NAME_SERIALIZED_CONTEXT_FILE engine variable
     * Create a new process with provided *processDefinitionId* and *businessKey*
     *
     * @param file
     * @param processDefinitionId
     * @param businessKey
     * @return the new processInstanceId
     */
    String processContextFileAndCreateProcessInstance(MultipartFile file, String processDefinitionId, String businessKey);

    /**
     * Retrieve Protools Context as JsonNode from of a given processInstance ID
     *
     * @param processInstanceId
     * @return the Json context of the process associated with process Instance
     * @throws BadContexMissingBPMNError
     */
    JsonNode getContextJsonNodeByProcessInstance(String processInstanceId);

    /**
     * Retrieve Protools Context as DTO from of a given processInstance ID
     *
     * @param processInstanceId
     * @return the Dto context of the process associated with process Instance
     * @throws BadContexMissingBPMNError
     */
    ContexteProcessus getContextDtoByProcessInstance(String processInstanceId);

}
