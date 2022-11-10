package com.protools.flowableDemo.services.coleman;

import org.springframework.stereotype.Service;

@Service
public interface PilotageCampaign {
    public abstract void createContext(PilotageCampaignContext context) throws Exception;
}
