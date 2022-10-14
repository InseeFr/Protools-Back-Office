package com.protools.flowableDemo.services.coleman.questionnaire;

public class MetadataValue {
    private Metadata value;

    public MetadataValue() {
    }

    public MetadataValue(Metadata value) {
        this.value = value;
    }

    public Metadata getValue() {
        return value;
    }

    public void setValue(Metadata value) {
        this.value = value;
    }
}
