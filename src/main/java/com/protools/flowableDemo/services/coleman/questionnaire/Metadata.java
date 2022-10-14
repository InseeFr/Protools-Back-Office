package com.protools.flowableDemo.services.coleman.questionnaire;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.Collection;

public class Metadata {
    private String context;

    private Collection<MetadataVariable> variables;

    public Metadata() {
    }

    public Metadata(String context, Collection<MetadataVariable> variables) {
        this.context = context;
        this.variables = variables;
    }

    @JsonGetter(value = "inseeContext")
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Collection<MetadataVariable> getVariables() {
        return variables;
    }

    public void setVariables(Collection<MetadataVariable> variables) {
        this.variables = variables;
    }
}
