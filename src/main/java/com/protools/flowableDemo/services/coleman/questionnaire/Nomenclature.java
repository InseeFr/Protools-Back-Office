package com.protools.flowableDemo.services.coleman.questionnaire;

import lombok.Data;

import java.util.Collection;

@Data
public class Nomenclature {
    private final String id;

    private final String label;

    private final Collection<?> value;
}
