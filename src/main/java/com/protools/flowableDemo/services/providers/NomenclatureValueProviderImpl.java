package com.protools.flowableDemo.services.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NomenclatureValueProviderImpl implements NomenclatureValueProvider {

    @Value("${fr.insee.nomenclature.value.provider.uri:#{null}}")
    private String nomenclatureValueProviderUri;

    @Autowired
    private ProviderRestTemplate restTemplate;

    @Override
    public Collection<?> getNomenclatureValue(String nomenclatureId) throws Exception {
        String uri = nomenclatureValueProviderUri + "/" + getPath(nomenclatureId);

        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

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
