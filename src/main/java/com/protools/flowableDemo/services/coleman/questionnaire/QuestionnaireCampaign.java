package com.protools.flowableDemo.services.coleman.questionnaire;

import org.springframework.stereotype.Service;

@Service
public interface QuestionnaireCampaign {
    public abstract void createContext(QuestionnaireCampaignContext context) throws Exception;
}
