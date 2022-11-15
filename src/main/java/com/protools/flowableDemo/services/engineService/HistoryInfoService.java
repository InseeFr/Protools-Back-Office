package com.protools.flowableDemo.services.engineService;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class HistoryInfoService {
    private Logger logger =LogManager.getLogger(HistoryInfoService.class);
    @Autowired
    private HistoryService historyService;

    @Autowired
    private ManagementService managementService;

    @Transactional
    public List<HistoricProcessInstance> getHistoryProcess(){
        return historyService.createHistoricProcessInstanceQuery()
                .finished().orderByProcessInstanceStartTime().desc().listPage(0,100);


    };

    @Transactional
    public List<HistoricTaskInstance> getHistoryTask(String processDefinitionID){
        return historyService.createHistoricTaskInstanceQuery()
                .processDefinitionId(processDefinitionID).listPage(0,100);
    };

    @Transactional
    public List<HistoricActivityInstance> getHistoryActivity(){
        return historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceStartTime().desc().listPage(0,100);

    }

    @Transactional
    public List<Job> getFailedJobs(){
        return managementService.createDeadLetterJobQuery().orderByJobCreateTime().desc().listPage(0,100);


    }

    @Transactional
    public List<Job> getSuspendedJobs(){
        return managementService.createSuspendedJobQuery().orderByJobCreateTime().desc().listPage(0,100);


    }

    @Transactional
    public List<HistoricProcessInstance> getDeletedProcess(){
        return historyService.createHistoricProcessInstanceQuery().deleted().orderByProcessInstanceStartTime().desc().listPage(0,100);

    }


}
