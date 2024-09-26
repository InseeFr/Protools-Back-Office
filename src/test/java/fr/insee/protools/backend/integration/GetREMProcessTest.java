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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ContextConfiguration(value = {
        "classpath:fr/insee/protools/backend/integration/springTypicalUsageTest-context.xml",
        "classpath:fr/insee/protools/backend/integration/springGetREMDelegate-context.xml"
})
@TestPropertySource(locations = "/application-test.properties")
//@ExtendWith(PluggableFlowableExtension.class)
@ExtendWith(FlowableSpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class GetREMProcessTest extends AbstractFlowableTestCase {

    final static String END_EVENT_ID ="endTask";

    private List<String> autoCleanedUpDeploymentIds = new ArrayList<>();

    @BeforeEach
    protected void setUp() throws Exception {
        this.autoCleanedUpDeploymentIds.clear();
    }

    @Test
    void test() {
        String delegateExpression = "${remGetPartitionListOfInterroPaginatedTask}";
        String taskId = "testedServiceTask";
        String processDefinitionId=deployBPMN("testProcess.bpmn20.xml",
                createOneServiceTaskProcess(delegateExpression, taskId));

        Map<String, Object> variables = new HashMap<>();
        variables.put(VARNAME_CURRENT_PARTITION_ID, "1");
        //Start the process
        ExecutionEntityImpl processInstance = (ExecutionEntityImpl) runtimeService.startProcessInstanceById(processDefinitionId,variables);

        //Verify
        assertThat(runtimeService.createProcessInstanceQuery().count()).isZero();
        String currentActivityId = ((ExecutionEntityImpl) processInstance).getExecutions().get(0).getCurrentActivityId();
        assertEquals(END_EVENT_ID, currentActivityId,"We expected the process to be on its end event");

        List<JsonNode> interroList=processInstance.getVariable(VARNAME_REM_INTERRO_LIST,List.class);
        Integer currentPage=processInstance.getVariable(VARNAME_INTERRO_LIST_PAGEABLE_CURRENT_PAGE,Integer.class);
        Boolean isLastPage=processInstance.getVariable(VARNAME_INTERRO_LIST_PAGEABLE_IS_LAST_PAGE,Boolean.class);
        assertEquals(RemServiceStub.defaultCurrentPage,currentPage);
        assertEquals(RemServiceStub.isLastPage,isLastPage);
        assertEquals(RemServiceStub.defaultInterroList,interroList);

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
