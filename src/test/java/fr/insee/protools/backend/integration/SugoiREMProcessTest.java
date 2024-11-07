package fr.insee.protools.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.integration.delegate_and_services_stub.RemServiceStub;
import org.flowable.bpmn.model.*;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.test.AbstractFlowableTestCase;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.spring.impl.test.FlowableSpringExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(value = {
        "classpath:fr/insee/protools/backend/integration/springTypicalUsageTest-context.xml",
        "classpath:fr/insee/protools/backend/integration/springSugoi-context.xml"


})
@TestPropertySource(locations = "/application-test.properties")
//@ExtendWith(PluggableFlowableExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@ExtendWith(MockitoExtension.class)
class SugoiREMProcessTest extends AbstractFlowableTestCase {

    static final String END_EVENT_ID ="endTask";
    static final String ctx_path ="fr/insee/protools/backend/integration/valid_ctx.json";

    private List<String> autoCleanedUpDeploymentIds = new ArrayList<>();

    @BeforeEach
    protected void setUp() throws Exception {
        this.autoCleanedUpDeploymentIds.clear();
    }

    @Test
    void test() throws IOException {
        String delegateExpression = "${sugoiREMCreateMissingAccountListTask}";
        String taskId = "testedServiceTask";
        String processDefinitionId=deployBPMN("testProcess.bpmn20.xml",
                createOneServiceTaskProcess(delegateExpression, taskId));

        Map<String, Object> variables = new HashMap<>();
        variables.put(VARNAME_CURRENT_PARTITION_ID, "1");

        String ctx= new String(getClass().getClassLoader().getResourceAsStream(ctx_path).readAllBytes(), StandardCharsets.UTF_8);
        variables.put(VARNAME_CONTEXT, ctx);

        //Start the process
        ExecutionEntityImpl processInstance = (ExecutionEntityImpl) runtimeService.startProcessInstanceById(processDefinitionId,variables);

        //Verify
        assertThat(runtimeService.createProcessInstanceQuery().count()).isZero();
        String currentActivityId = ((ExecutionEntityImpl) processInstance).getExecutions().get(0).getCurrentActivityId();
        assertEquals(END_EVENT_ID, currentActivityId,"We expected the process to be on its end event");

        Map<String,String> pwdByInterroId=processInstance.getVariable(VARNAME_DIRECTORYACCESS_PWD_FOR_INTERRO_ID_MAP,Map.class);
        assertEquals(new HashSet<>(RemServiceStub.defaultinterrogationIdWithoutAccountList), pwdByInterroId.keySet(), "Lists A and B contain logically equal elements but different instances");
    }

    private BpmnModel createOneServiceTaskProcess(String delegateExpression, String taskId) {
        BpmnModel model = new BpmnModel();
        org.flowable.bpmn.model.Process process = new org.flowable.bpmn.model.Process();
        model.addProcess(process);

        process.setId("oneTaskProcess");
        process.setName("The one task process");

        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        process.addFlowElement(startEvent);

        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setName("The org.flowable.task.service.Task");
        serviceTask.setId(taskId);
        serviceTask.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION);
        serviceTask.setImplementation(delegateExpression);
        process.addFlowElement(serviceTask);

        EndEvent endEvent = new EndEvent();
        endEvent.setId(END_EVENT_ID);
        process.addFlowElement(endEvent);

        process.addFlowElement(new SequenceFlow("start", taskId));
        process.addFlowElement(new SequenceFlow(taskId, END_EVENT_ID));

        return model;
    }


    private String deployBPMN(String ressourceName,BpmnModel model){

        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.addBpmnModel(ressourceName,model);
        Deployment deployment = deploymentBuilder.deploy();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId()).singleResult();
        return processDefinition.getId();
    }
}
