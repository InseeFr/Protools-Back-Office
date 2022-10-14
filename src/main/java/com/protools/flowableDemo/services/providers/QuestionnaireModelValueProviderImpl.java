package com.protools.flowableDemo.services.providers;

import com.protools.flowableDemo.enums.CollectionPlatform;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class QuestionnaireModelValueProviderImpl implements QuestionnaireModelValueProvider {

    @Value("${questionnaire.model.value.provider.uri:#{null}}")
    private String questionnaireModelValueProviderUri;

    @Override
    public Map<?, ?> getQuestionnaireModelValue(CollectionPlatform platform, String questionnaireModelId)
            throws Exception {
        String uri = questionnaireModelValueProviderUri + "/" + getPath(platform, questionnaireModelId);

        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, converter);

        ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

    private String getPath(CollectionPlatform platform, String questionnaireModelId) {
        return platform.name().toLowerCase() + "/" + questionnaireModelId + ".json";
    }

}
