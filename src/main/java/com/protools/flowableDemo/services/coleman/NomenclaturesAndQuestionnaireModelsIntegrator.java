package com.protools.flowableDemo.services.coleman;

import com.protools.flowableDemo.dto.Nomenclature;
import com.protools.flowableDemo.dto.QuestionnaireModel;

import java.util.Collection;

public interface NomenclaturesAndQuestionnaireModelsIntegrator {
    public abstract void execute(Collection<QuestionnaireModel> models,
                                 Collection<Nomenclature> nomenclatures) throws Exception;
}
