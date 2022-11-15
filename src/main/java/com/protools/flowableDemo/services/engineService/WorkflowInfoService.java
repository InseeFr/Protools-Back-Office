package com.protools.flowableDemo.services.engineService;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.*;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.job.service.JobService;
import org.flowable.task.api.Task;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WorkflowInfoService {
    private Logger logger =LogManager.getLogger(WorkflowInfoService.class);
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    ProcessEngine processEngine;
    @Transactional
    public Map<String, FlowElement> getBPMNModel(String processDefinitionId){
        BpmnModel response = repositoryService.getBpmnModel(processDefinitionId);
        return (response.getMainProcess().getFlowElementMap());
    };

    @Transactional
    public Map<String, List<ExtensionElement>> getExtension(String processDefinitionId){
        BpmnModel response = repositoryService.getBpmnModel(processDefinitionId);
        return (response.getMainProcess().getExtensionElements());
    };

    @Transactional
    public String getBPMNModelDocumentation(String processDefinitionId){
        BpmnModel response = repositoryService.getBpmnModel(processDefinitionId);

        return (response.getMainProcess().getDocumentation());
    };
    @Transactional
    public JSONObject getAllProcessInstance(){
        List<ProcessInstance> liste = runtimeService.createProcessInstanceQuery()
                .list();
        JSONObject responseDetailsJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i =0; i<liste.size(); i++) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", liste.get(i).getId());
            jsonResponse.put("name", liste.get(i).getName());
            jsonResponse.put("processKey", liste.get(i).getProcessDefinitionKey());
            jsonResponse.put("activity", liste.get(i).getActivityId());
            jsonResponse.put("startTime",liste.get(i).getStartTime());
            jsonResponse.put("ProcessDefinitionId",liste.get(i).getProcessDefinitionId());
            jsonResponse.put("description", liste.get(i).getDescription());
            jsonResponse.put("businessStatus", liste.get(i).getBusinessStatus());
            jsonResponse.put("businessKey", liste.get(i).getBusinessKey());
            BpmnModel response = repositoryService.getBpmnModel(liste.get(i).getProcessDefinitionId());
            jsonResponse.put("documentation", response.getMainProcess().getDocumentation());
            jsonResponse.put("isSuspended", liste.get(i).isSuspended());
            jsonResponse.put("isEnded", liste.get(i).isEnded());
            List<Job> deadLetterList = processEngine.getManagementService().createDeadLetterJobQuery().processInstanceId(liste.get(i).getId()).list();
            jsonResponse.put("deadLetterList", deadLetterList.size());

            jsonArray.put(jsonResponse);

        }
        responseDetailsJson.put("processes", jsonArray);
        return responseDetailsJson;
    }
    @Transactional
    public JSONArray getTasks(String assignee) {
        List<Task> response = taskService.createTaskQuery().taskAssignee(assignee).list();
        JSONArray jsonArray = new JSONArray();
        logger.info("Ceci est un test pour voir s'il prend en compte la dernière version du code");
        for (int i =0; i<response.size(); i++) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("TaskId", response.get(i).getId());
            jsonResponse.put("name", response.get(i).getName());
            jsonResponse.put("processInstance", response.get(i).getProcessInstanceId());
            jsonResponse.put("createTime",response.get(i).getCreateTime());
            jsonResponse.put("processDefinitionID",response.get(i).getProcessDefinitionId());
            jsonResponse.put("description", response.get(i).getDescription());
            jsonResponse.put("category",response.get(i).getCategory());
            jsonResponse.put("assignee",response.get(i).getAssignee());
            jsonArray.put(jsonResponse);
        }
        return jsonArray;
    }

    @Transactional
    public JSONArray getTasksProcess(String ProcessID) {
        List<Task> response = taskService.createTaskQuery().processInstanceId(ProcessID).list();
        JSONArray jsonArray = new JSONArray();
        for (int i =0; i<response.size(); i++) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("TaskId", response.get(i).getId());
            jsonResponse.put("name", response.get(i).getName());
            jsonResponse.put("processInstance", response.get(i).getProcessInstanceId());
            jsonResponse.put("delegationState", response.get(i).getDelegationState());
            jsonResponse.put("parentTask",response.get(i).getParentTaskId());
            jsonResponse.put("createTime",response.get(i).getCreateTime());
            jsonResponse.put("BPMN_ID", response.get(i).getExecutionId());
            jsonResponse.put("processDefinitionID",response.get(i).getProcessDefinitionId());
            jsonResponse.put("description", response.get(i).getDescription());
            jsonResponse.put("category",response.get(i).getCategory());
            jsonArray.put(jsonResponse);

        }
        return jsonArray;
    }

    @Transactional
    public String getProcessDefinitionID(String ProcessID){
        ProcessInstance response = runtimeService.createProcessInstanceQuery().processInstanceId(ProcessID).singleResult();
        return response.getProcessDefinitionId();
    }
    @Transactional
    public JSONArray getAllTasks() {
        List<Task> response = taskService.createTaskQuery().list();

        JSONArray jsonArray = new JSONArray();
        for (int i =0; i<response.size(); i++) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("TaskId", response.get(i).getId());
            jsonResponse.put("name", response.get(i).getName());
            jsonResponse.put("processInstance", response.get(i).getProcessInstanceId());
            jsonResponse.put("delegationState", response.get(i).getDelegationState());
            jsonResponse.put("parentTask",response.get(i).getParentTaskId());
            jsonResponse.put("createTime",response.get(i).getCreateTime());
            jsonResponse.put("extensionID", response.get(i).getExecutionId());
            jsonResponse.put("description", response.get(i).getDescription());
            jsonResponse.put("category",response.get(i).getCategory());
            jsonArray.put(jsonResponse);

        }
        return jsonArray;

    }

    @Transactional
    public JSONArray getJobs(String processInstanceID){
        JSONArray jsonArray = new JSONArray();

        JobService jobService = processEngine.getProcessEngineConfiguration().getAsyncExecutor().getJobServiceConfiguration().getJobService();

        List<Job> jobs = jobService.createJobQuery()
                .processInstanceId(processInstanceID)
                .list();

        for (int i =0; i<jobs.size(); i++) {
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("retries", jobs.get(i).getRetries());
            jsonResponse.put("message",jobs.get(i).getExceptionMessage());
            jsonArray.put(jsonResponse);

        }
        return jsonArray;
    }

    @Transactional
    public Map<String, Object> getProcessVariables(String ProcessInstanceID){
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().includeProcessVariables().processInstanceId(ProcessInstanceID).singleResult();
        return processInstance.getProcessVariables();
    }

    @Transactional
    public List<String> getActivityExecution(String ProcessInstanceID){
        List<Execution> executions = runtimeService.createExecutionQuery().onlyChildExecutions().processInstanceId(ProcessInstanceID).list();
        return executions.stream().map(Execution::getActivityId).collect(Collectors.toList());
    }

    @Transactional
    public List<Execution> getAllExecutions(){
        List<Execution> allExecutions = runtimeService.createExecutionQuery().list();
        return allExecutions;
    }

}
