package com.protools.flowableDemo.services.providers;

import com.protools.flowableDemo.enums.CollectionPlatform;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;

import java.lang.reflect.Field;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireModelValueProviderImplTest {
    @Mock
    private ProviderRestTemplate restTemplate;

    @InjectMocks
    private QuestionnaireModelValueProviderImpl provider = new QuestionnaireModelValueProviderImpl();

    @Test
    public void givenMockingIsDoneByMockito_whenExchangeIsCalled_shouldReturnMockedObject() throws Exception {
        String questionnaireModelValueProviderUri = "https://questionnaire-model-provider/QuestionnaireModels";

        Field field = provider.getClass().getDeclaredField("questionnaireModelValueProviderUri");
        field.setAccessible(true);
        field.set(provider, questionnaireModelValueProviderUri);

        CollectionPlatform platform = CollectionPlatform.coleman;

        String questionnaireModelId = "model_1";

        String uri = questionnaireModelValueProviderUri + "/coleman/" + questionnaireModelId + ".json";

        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        Map<?, ?> questionnaireModelValue = Map.of("id", "id_1", "model", "model_1");

        Mockito
                .when(restTemplate.exchange(uri, HttpMethod.GET, request, Map.class))
                .thenReturn(new ResponseEntity<>(questionnaireModelValue, HttpStatus.OK));

        Map<?, ?> providedQuestionnaireModelValue = provider.getQuestionnaireModelValue(platform, questionnaireModelId);

        Assertions.assertEquals(questionnaireModelValue, providedQuestionnaireModelValue);
    }
}

