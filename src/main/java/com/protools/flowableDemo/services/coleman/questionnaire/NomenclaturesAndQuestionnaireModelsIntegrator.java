package com.protools.flowableDemo.services.coleman.questionnaire;

import java.util.Collection;
import java.util.Map;

public interface NomenclaturesAndQuestionnaireModelsIntegrator {
    public abstract void execute(
            Collection<String> questionnaireModelIds,
            Map<String, String> questionnaireModelLabelsMappedById,
            Map<String, Collection<String>> requiredNomenclatureIdsMappedByQuestionnaireModelId,
            Map<String, String> nomenclatureLabelsMappedById) throws Exception;
}
