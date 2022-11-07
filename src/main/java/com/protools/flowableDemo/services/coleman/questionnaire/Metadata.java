package com.protools.flowableDemo.services.coleman.questionnaire;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class Metadata {
    private final String context;

    private final Collection<MetadataVariable> variables;

    @JsonGetter(value = "inseeContext")
    public String getContext() {
        return context;
    }

    @JsonGetter(value = "value")
    public Map<String, Collection<MetadataVariable>> getVariables() {
        return Map.of("variables", variables);
    }
}
