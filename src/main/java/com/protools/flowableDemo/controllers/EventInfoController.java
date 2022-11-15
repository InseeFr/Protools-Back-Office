package com.protools.flowableDemo.controllers;

import com.protools.flowableDemo.services.engineService.EventInfoService;
import io.swagger.v3.oas.annotations.Operation;
import org.flowable.eventregistry.api.EventDeployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class EventInfoController {
    @Autowired
    private EventInfoService eventInfoService;

    @CrossOrigin
    @Operation(summary = "Get Events ")
    @GetMapping(value = "/eventDeployments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventDeployment> getEventActivity(){
        return(eventInfoService.getEventDeployments());
    }

}
