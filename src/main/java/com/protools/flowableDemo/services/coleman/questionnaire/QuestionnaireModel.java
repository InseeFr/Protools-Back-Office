package com.protools.flowableDemo.services.coleman.questionnaire;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class QuestionnaireModel {
    private final String id;

    private final String label;

    private final Collection<String> requiredNomenclatureIds;

    private final Map<?, ?> value;

    @JsonGetter(value = "idQuestionnaireModel")
    public String getId() {
        return id;
    }
}
