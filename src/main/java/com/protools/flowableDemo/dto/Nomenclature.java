package com.protools.flowableDemo.dto;

import lombok.Data;

import java.util.Collection;

@Data
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
}
