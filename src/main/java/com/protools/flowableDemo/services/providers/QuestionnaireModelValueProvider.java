package com.protools.flowableDemo.services.providers;

import com.protools.flowableDemo.enums.CollectionPlatform;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface QuestionnaireModelValueProvider {
    public abstract Map<?, ?> getQuestionnaireModelValue(CollectionPlatform platform, String questionnaireModelId)
            throws Exception;
}
