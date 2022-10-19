package com.protools.flowableDemo.services.coleman.pilotage;

import org.springframework.stereotype.Service;

@Service
public interface PilotageCampaign {
    public abstract void createContext(PilotageCampaignContext context) throws Exception;
}
