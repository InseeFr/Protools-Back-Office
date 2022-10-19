package com.protools.flowableDemo.services.coleman.questionnaire;

import com.fasterxml.jackson.annotation.JsonGetter;

import java.util.Collection;

public class QuestionnaireCampaignContext {
    private String id;
    private String label;
    private MetadataValue metadata;
    private Collection<String> questionnaireModelIds;

    public QuestionnaireCampaignContext() {
    }

    public QuestionnaireCampaignContext(String id, String label, MetadataValue metadata, Collection<String> questionnaireModelIds) {
        this.id = id;
        this.label = label;
        this.metadata = metadata;
        this.questionnaireModelIds = questionnaireModelIds;
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

    public MetadataValue getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataValue metadata) {
        this.metadata = metadata;
    }

    @JsonGetter(value = "questionnaireIds")
    public Collection<String> getQuestionnaireModelIds() {
        return questionnaireModelIds;
    }

    public void setQuestionnaireModelIds(Collection<String> questionnaireModelIds) {
        this.questionnaireModelIds = questionnaireModelIds;
    }
}
