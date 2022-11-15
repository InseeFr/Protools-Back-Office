package com.protools.flowableDemo.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@JacksonXmlRootElement(localName = "ModeleQuestionnaire")
public class QuestionnaireModel {
    private String id;

    private String label;

    @JacksonXmlProperty(localName = "NomenclaturesRequises")
    private Collection<String> requiredNomenclatureIds;

    private Map<?, ?> value;

    public QuestionnaireModel(String id, String label, Collection<String> requiredNomenclatureIds, Map<?, ?> value) {
        this.id = id;
        this.label = label;
        this.requiredNomenclatureIds = requiredNomenclatureIds;
        this.value = value;
    }

    public void setRequiredNomenclatureIds(Collection<Nomenclature> requiredNomenclatures) {
        this.requiredNomenclatureIds = requiredNomenclatures.stream().map(Nomenclature::getId).collect(
                Collectors.toList());
    }

    @JsonGetter(value = "idQuestionnaireModel")
    public String getId() {
        return id;
    }
}
