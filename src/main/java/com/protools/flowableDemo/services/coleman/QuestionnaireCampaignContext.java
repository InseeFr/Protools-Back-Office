package com.protools.flowableDemo.services.coleman;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.protools.flowableDemo.dto.ContextAndVariables;
import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class QuestionnaireCampaignContext {
    private final String id;

    private final String label;

    private final ContextAndVariables contextAndVariables;

    private final Collection<String> questionnaireModelIds;

    @JsonGetter(value = "metadata")
    public Map<String, ContextAndVariables> getContextAndVariables() {
        return Map.of("value", contextAndVariables);
    }

    @JsonGetter(value = "questionnaireIds")
    public Collection<String> getQuestionnaireModelIds() { return questionnaireModelIds; }
}
