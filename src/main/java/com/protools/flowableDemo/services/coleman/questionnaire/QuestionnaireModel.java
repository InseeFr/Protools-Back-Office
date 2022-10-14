package com.protools.flowableDemo.services.coleman.questionnaire;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.Collection;
import java.util.Map;

public class QuestionnaireModel {
    private String id;
    private String label;
    private Collection<String> requiredNomenclatureIds;
    private Map<?, ?> value;

    public QuestionnaireModel() {
    }

    public QuestionnaireModel(String id, String label, Collection<String> requiredNomenclatureIds, Map<?, ?> value) {
        this.id = id;
        this.label = label;
        this.requiredNomenclatureIds = requiredNomenclatureIds;
        this.value = value;
    }

    @JsonGetter(value = "idQuestionnaireModel")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Collection<String> getRequiredNomenclatureIds() {
        return requiredNomenclatureIds;
    }

    public void setRequiredNomenclatureIds(Collection<String> requiredNomenclatureIds) {
        this.requiredNomenclatureIds = requiredNomenclatureIds;
    }

    public Map<?, ?>  getValue() {
        return value;
    }

    public void setValue(Map<?, ?>  value) {
        this.value = value;
    }
}
