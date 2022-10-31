package com.protools.flowableDemo.services.providers;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class ProviderRestTemplate extends RestTemplate {
    public ProviderRestTemplate() {
        super();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(MediaType.TEXT_PLAIN));

        this.getMessageConverters().add(0, converter);
    }
}
