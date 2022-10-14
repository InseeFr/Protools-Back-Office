package com.protools.flowableDemo.services.coleman.questionnaire;

import java.util.Collection;

public class Nomenclature {
    private String id;
    private String label;
    private Collection<?> value;

    public Nomenclature() {
    }

    public Nomenclature(String id, String label, Collection<?> value) {
        this.id = id;
        this.label = label;
        this.value = value;
    }

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

    public Collection<?> getValue() {
        return value;
    }

    public void setValue(Collection<?> value) {
        this.value = value;
    }
}
