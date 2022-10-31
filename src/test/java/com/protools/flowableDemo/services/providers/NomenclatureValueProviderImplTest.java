package com.protools.flowableDemo.services.providers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class NomenclatureValueProviderImplTest {
    @Mock
    private ProviderRestTemplate restTemplate;

    @InjectMocks
    private NomenclatureValueProviderImpl provider = new NomenclatureValueProviderImpl();

    @Test
    public void givenMockingIsDoneByMockito_whenExchangeIsCalled_shouldReturnMockedObject() throws Exception {
        String nomenclatureValueProviderUri = "https://nomenclature-provider/Nomenclatures";

        Field field = provider.getClass().getDeclaredField("nomenclatureValueProviderUri");
        field.setAccessible(true);
        field.set(provider, nomenclatureValueProviderUri);

        String nomenclatureId = "L_UPLOADED_NOMENCLATURE-1-0-0";

        String uri = nomenclatureValueProviderUri + "/UPLOADED_NOMENCLATURE/" + nomenclatureId + ".json";

        HttpEntity<String> request = new HttpEntity<>(new HttpHeaders());

        Collection<?> nomenclatureValue = List.of("element-1", "element-2");

        Mockito
                .when(restTemplate.exchange(uri, HttpMethod.GET, request, Collection.class))
                .thenReturn(new ResponseEntity<>(nomenclatureValue, HttpStatus.OK));

        Collection<?> providedNomenclatureValue = provider.getNomenclatureValue(nomenclatureId);

        Assertions.assertEquals(nomenclatureValue, providedNomenclatureValue);
    }
}

