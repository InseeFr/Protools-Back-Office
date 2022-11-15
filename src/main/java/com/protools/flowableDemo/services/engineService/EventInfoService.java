package com.protools.flowableDemo.services.engineService;

import org.flowable.eventregistry.api.EventDeployment;
import org.flowable.eventregistry.api.EventRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventInfoService {
    @Autowired
    private EventRepositoryService eventRepositoryService;

    @Transactional
    public List<EventDeployment> getEventDeployments(){
        List<EventDeployment> response = eventRepositoryService.createDeploymentQuery().list();
        return (response);
    };
}
