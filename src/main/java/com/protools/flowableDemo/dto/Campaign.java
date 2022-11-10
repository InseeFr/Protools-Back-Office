package com.protools.flowableDemo.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.protools.flowableDemo.enums.Context;
import lombok.Data;

import java.util.Collection;

@Data
@JacksonXmlRootElement(localName = "Campagne")
public class Campaign {
    private String id;

    private String label;

    @JacksonXmlProperty(localName = "Contexte")
    private Context context;

    @JacksonXmlProperty(localName = "Metadonnees")
    private Collection<Variable> variables;

    private Collection<Partition> partitions;

    @JacksonXmlProperty(localName = "ModelesQuestionnaires")
    private Collection<QuestionnaireModel> questionnaireModels;

    private Collection<Nomenclature> nomenclatures;
}
