package com.protools.flowableDemo.services.coleman.questionnaire;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.util.Collection;

@Data
public class QuestionnaireCampaignContext {
    private final String id;

    private final String label;

    private final Metadata metadata;

    private final Collection<String> questionnaireModelIds;

    @JsonGetter(value = "questionnaireIds")
    public Collection<String> getQuestionnaireModelIds() {
        return questionnaireModelIds;
    }
}
