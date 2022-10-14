package com.protools.flowableDemo.services.coleman.questionnaire;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public interface Campaign {
    public abstract void createContext(
            CampaignContext context,
            Map<String, String>nomenclatureLabelsMappedById,
            Map<String, String>questionnaireModelLabelsMappedById,
            Map<String, Collection<String>>requiredNomenclatureIdsMappedByQuestionnaireModelId) throws Exception;
}
