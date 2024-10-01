package fr.insee.protools.backend.service.context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import fr.insee.protools.backend.dto.ContexteProcessus;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.context.exception.BadContexMissingBPMNError;
import fr.insee.protools.backend.service.context.exception.BadContextIOException;
import fr.insee.protools.backend.service.context.exception.BadContextIncorrectBPMNError;
import fr.insee.protools.backend.service.context.exception.BadContextNotJSONBPMNError;
import fr.insee.protools.backend.service.exception.ProcessDefinitionNotFoundException;
import fr.insee.protools.backend.service.exception.TaskNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CONTEXT;

@Service
@Slf4j
@RequiredArgsConstructor
public class ContextServiceImpl implements IContextService {

    private static final ObjectReader defaultReader = new ObjectMapper().registerModule(new JavaTimeModule()).reader(); // maybe with configs
    //Key : processInstanceID
    //Value: Pair <raw json of Context as String; Dto representing this json Context>
    private static final Map<String, ContextPair> contextCache = new ConcurrentHashMap<>();
    //TODO: Peut être que ca va sortir dans une dépendance externe
    private static final String SCHEMA_VALIDATION_FILE = "/schema/contexte-processus.json";
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final ApplicationContext springApplicationContext;
    private static final JsonSchema contextJsonSchema = JsonSchemaFactory
            .getInstance(SpecVersion.VersionFlag.V202012)
            .getSchema(ContextServiceImpl.class.getResourceAsStream(SCHEMA_VALIDATION_FILE));

    @Override
    public void processContextFileAndCompleteTask(MultipartFile file, String taskId) {
        //Check if task exists
        if (StringUtils.isBlank(taskId)) {
            log.error("taskId is null or blank");
            throw new TaskNotFoundException(taskId);
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null || task.getProcessInstanceId() == null) {
            log.error("taskId={} does not exist", taskId);
            throw new TaskNotFoundException(taskId);
        }

        //check context
        ContextPair contextPair = processContextFile(file, task.getProcessDefinitionId());
        //Store context in cache
        contextCache.put(task.getProcessInstanceId(), contextPair);

        // Complete task and store context raw json as string within process variables
        Map<String, Object> variables = new HashMap<>();
        variables.put(VARNAME_CONTEXT, contextPair.contextAsString);
        taskService.complete(taskId, variables);
    }

    @Override
    public String processContextFileAndCreateProcessInstance(MultipartFile file, String processDefinitionId, String businessKey) {
        if (StringUtils.isBlank(processDefinitionId)) {
            log.error("processDefinitionId is null or blank");
            throw new ProcessDefinitionNotFoundException(processDefinitionId);
        }

        try {
            //check context
            ContextPair contextPair = processContextFile(file, processDefinitionId);
            //Create process instance
            ProcessInstance processInstance;
            Map<String, Object> variables = new HashMap<>();
            variables.put(VARNAME_CONTEXT, contextPair.contextAsString);

            if (StringUtils.isBlank(businessKey)) {
                processInstance = runtimeService.startProcessInstanceByKey(processDefinitionId, variables);
            } else {
                processInstance = runtimeService.startProcessInstanceByKey(processDefinitionId, businessKey, variables);
            }
            log.info("Created new process instance with processDefinitionId={} - ProcessInstanceId={}", processDefinitionId, processInstance.getProcessInstanceId());
            //Store context in cache
            contextCache.put(processInstance.getProcessInstanceId(), contextPair);

            return processInstance.getProcessInstanceId();
        } catch (FlowableObjectNotFoundException e) {
            log.error("processDefinitionId={} is unknown", processDefinitionId);
            throw new ProcessDefinitionNotFoundException(processDefinitionId);
        }
    }

    @Override
    public JsonNode getContextJsonNodeByProcessInstance(String processInstanceId) {
        try {
            return defaultReader.readTree(getContextByProcessInstance(processInstanceId).contextAsString);
        } catch (JsonProcessingException e) {
            throw new BadContextIncorrectBPMNError(String.format("Context retrieved from engine cannot be parsed for processInstanceId=[%s] ", processInstanceId));
        }
    }

    @Override
    public ContexteProcessus getContextDtoByProcessInstance(String processInstanceId) {
        return getContextByProcessInstance(processInstanceId).contextSchema();
    }

    private ContextPair getContextByProcessInstance(String processInstanceId) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new FlowableObjectNotFoundException("Could not find a process instance with id '" + processInstanceId + "'.", ProcessInstance.class);
        }

        ContextPair result = contextCache.get(processInstanceId);
        //If value does not exist in cache yet : Retrieve it and update cache
        if (result == null) {
            String contextStr = runtimeService.getVariable(processInstanceId, VARNAME_CONTEXT, String.class);
            if (contextStr == null || contextStr.isBlank()) {
                throw new BadContexMissingBPMNError(String.format("Context retrieved from engine is null or empty processInstanceId=[%s] ", processInstanceId));
            }
            try {
                ContexteProcessus schema = defaultReader.readValue(contextStr, ContexteProcessus.class);
                result = new ContextPair(contextStr, schema);
                contextCache.put(processInstanceId, result);
            } catch (IOException e) {
                throw new BadContextIncorrectBPMNError(String.format("Context cannot be parsed for processInstanceId=[%s] ", processInstanceId));
            }
        }
        return result;
    }

    protected ContexteProcessus jsonReadAndSchemaValidation(JsonNode rootContext) {
        //Validate that the Json is valid regarding the json-schema
        Set<ValidationMessage> jsonValidationErrors = contextJsonSchema.validate(rootContext);
        if (!jsonValidationErrors.isEmpty()) {
            String message = String.format("Uploaded context is not correct according to the json-schema. Errors: %s",
                    jsonValidationErrors);
            log.warn(message);
            throw new BadContextNotJSONBPMNError(message);
        } else {
            try {
                return defaultReader.treeToValue(rootContext, ContexteProcessus.class);
            } catch (IllegalArgumentException | JsonProcessingException e) {
                log.warn("Uploaded context cannot be parsed correctly {}", e.getMessage());
                throw new BadContextNotJSONBPMNError("Uploaded context cannot be parsed correctly");
            }
        }
    }

    protected ContextPair processContextFile(MultipartFile file, String processDefinitionKey) {
        //Validate file name (JSON)
        var fileExtension = getFileExtension(file.getOriginalFilename());
        if (fileExtension.isEmpty()) {
            throw new BadContextNotJSONBPMNError(String.format("Uploaded file %s has incorrect filename without extension", file.getOriginalFilename()));
        } else if (!fileExtension.get().equalsIgnoreCase("json")) {
            throw new BadContextNotJSONBPMNError(String.format("Uploaded file %s has incorrect extension. Expected json", file.getOriginalFilename()));
        }

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.trace("Context File content : " + content);

            JsonNode rootContext = defaultReader.readTree(content);
            ContexteProcessus contexte = jsonReadAndSchemaValidation(rootContext);

            Set<String> contextErrors = isContextOKForBPMN(processDefinitionKey, contexte);
            if (!contextErrors.isEmpty()) {
                throw new BadContextIncorrectBPMNError(contextErrors.toString());
            }
            log.info("idCampaign={}", contexte.getId());

            return new ContextPair(content, contexte);
        } catch (IOException | IllegalArgumentException e) {
            throw new BadContextIOException("Error while reading context content: " + e.getMessage(), e);
        }
    }

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    /**
     * Check if protoolsContextRootNode allows every Task implementing {@link  fr.insee.protools.backend.service.DelegateContextVerifier#getContextErrors  DelegateContextVerifier}  interface to run correctly
     *
     * @param processDefinitionKey    The process (BPMN) identifier
     * @param contexteProcessus The context to check
     * @return A list of the problems found
     * @throws FlowableObjectNotFoundException if no process definition (BPMN) matches processDefinitionKey
     */
    public Set<String> isContextOKForBPMN(String processDefinitionKey, ContexteProcessus contexteProcessus) {
        //TODO: verify also that delegates exists

        //At least, the campaign ID should be defined so we can write it on process variables to be used un groovy scripts
        Set<String> errors = new HashSet<>();
        if(contexteProcessus.getId()==null){
            errors.add("id is missing");
        }
        if(contexteProcessus.getMetadonnees()==null){
            errors.add("metadonnees is missing");
        }

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        processDefinitionQuery.processDefinitionKey(processDefinitionKey);
        processDefinitionQuery.latestVersion();
        ProcessDefinition definition = processDefinitionQuery.singleResult();
        if (definition == null) {
            throw new FlowableObjectNotFoundException("Cannot find process definition with key " + processDefinitionKey, ProcessDefinition.class);
        }
        BpmnModel model = repositoryService.getBpmnModel(definition.getId());
        if (model == null) {
            throw new FlowableObjectNotFoundException("Cannot find process BPMN model definition with key " + processDefinitionKey, ProcessDefinition.class);
        }

        org.flowable.bpmn.model.Process processModel = model.getProcessById(processDefinitionKey);
        if (processModel == null) {
            throw new FlowableObjectNotFoundException("Cannot find process Model with key " + processDefinitionKey, ProcessDefinition.class);
        }

        processModel.getFlowElements().stream()
                .filter(flowElement -> (flowElement instanceof ServiceTask || flowElement instanceof SubProcess))
                .forEach(flowElement -> errors.addAll(analyseProcess(flowElement, contexteProcessus)));
        return errors;
    }

    private Set<String> analyseProcess(FlowElement flowElement, ContexteProcessus contexteProcessus) {
        if (flowElement instanceof ServiceTask serviceTask) {
            if (serviceTask.getImplementationType().equals("delegateExpression")) {
                String delegateExpression = serviceTask.getImplementation().replace("${", "").replace("}", "");
                try {
                    Object bean = springApplicationContext.getBean(delegateExpression);
                    if (bean instanceof DelegateContextVerifier beanDelegateCtxVerifier) {
                        return beanDelegateCtxVerifier.getContextErrors(contexteProcessus);
                    }
                } catch (NoSuchBeanDefinitionException e) {
                }
            }
        } else if (flowElement instanceof SubProcess subProcessFlowElement) {
            Set<String> errors = new HashSet<>();
            subProcessFlowElement.getFlowElements()
                    .forEach(subFlowElement -> errors.addAll(analyseProcess(subFlowElement, contexteProcessus)));
            return errors;
        }
        return Set.of();
    }

    protected record ContextPair(String contextAsString, ContexteProcessus contextSchema) {
    }
}
