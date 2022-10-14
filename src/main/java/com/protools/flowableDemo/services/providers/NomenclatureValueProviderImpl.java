package com.protools.flowableDemo.services.providers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NomenclatureValueProviderImpl implements NomenclatureValueProvider {

    @Value("${nomenclature.value.provider.uri:#{null}}")
    private String nomenclatureValueProviderUri;

    @Override
    public Collection<?> getNomenclatureValue(String nomenclatureId) throws Exception {
        String uri = nomenclatureValueProviderUri + "/" + getPath(nomenclatureId);

        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, converter);

        ResponseEntity<Collection> response = restTemplate.exchange(uri, HttpMethod.GET, request, Collection.class);

        return response.getBody();
    }

    private String getPath(String nomenclatureId) throws Exception {
        Matcher matcher = Pattern.compile("^._(.*)-\\d+-\\d+-\\d+$").matcher(nomenclatureId);

        if (!matcher.find()) {
            throw new Exception(String.format("nomenclature id %s doesn't match", nomenclatureId));
        }

        return matcher.group(1) +  "/" + nomenclatureId + ".json";
    }

}
