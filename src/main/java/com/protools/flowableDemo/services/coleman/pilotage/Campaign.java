package com.protools.flowableDemo.services.coleman.pilotage;

import org.springframework.stereotype.Service;

@Service
public interface Campaign {
    public abstract void createContext(CampaignContext context) throws Exception;
}
