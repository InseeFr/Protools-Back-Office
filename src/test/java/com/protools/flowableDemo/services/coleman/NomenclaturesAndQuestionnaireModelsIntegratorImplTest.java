package com.protools.flowableDemo.services.coleman;

import com.protools.flowableDemo.dto.Nomenclature;
import com.protools.flowableDemo.dto.QuestionnaireModel;
import com.protools.flowableDemo.enums.CollectionPlatform;
import com.protools.flowableDemo.services.authentification.KeycloakService;
import com.protools.flowableDemo.services.providers.NomenclatureValueProvider;
import com.protools.flowableDemo.services.providers.QuestionnaireModelValueProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class NomenclaturesAndQuestionnaireModelsIntegratorImplTest {
    @Mock
    private NomenclatureValueProvider nomenclatureValueProvider;

    @Mock
    private QuestionnaireModelValueProvider questionnaireModelValueProvider;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NomenclaturesAndQuestionnaireModelsIntegratorImpl integrator =
            new NomenclaturesAndQuestionnaireModelsIntegratorImpl();

    @Test
    public void givenMockingIsDoneByMockito_whenIntegrationIsExecuted_shouldPostNomenclaturesAnQuestionnaireModels()
            throws Exception {
        String colemanQuestionnaireUri = "https://coleman-questionnaire";

        Field field = integrator.getClass().getDeclaredField("colemanQuestionnaireUri");
        field.setAccessible(true);
        field.set(integrator, colemanQuestionnaireUri);

        String token = "token-value";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Mockito
                .when(keycloakService.getContextReferentialToken())
                .thenReturn(token);

        Collection<String> questionnaireModelIds = List.of("model-1", "model-2", "model-3");

        Collection<QuestionnaireModel> models = List.of(
                new QuestionnaireModel("model-1","label-model-1",
                        List.of("nomenclature-1", "nomenclature-2"), Map.of()),
                new QuestionnaireModel("model-2","label-model-3",
                        List.of(), Map.of()),
                new QuestionnaireModel("model-3","label-model-3",
                        List.of("nomenclature-2", "nomenclature-3"), Map.of())
        );

        Collection<String> allRequiredNomenclaturesIds = List.of("nomenclature-1", "nomenclature-2", "nomenclature-3");

        List<Nomenclature> nomenclatures = List.of(
                new Nomenclature("nomenclature-1", "label-nomenclature-1", List.of()),
                new Nomenclature("nomenclature-2", "label-nomenclature-2", List.of()),
                new Nomenclature("nomenclature-3", "label-nomenclature-3", List.of())
        );

        integrator.execute(models, nomenclatures);

        Mockito.verify(nomenclatureValueProvider, Mockito.times(allRequiredNomenclaturesIds.size())).
                getNomenclatureValue(Mockito.anyString());

        for (String id : allRequiredNomenclaturesIds) {
            Mockito.verify(nomenclatureValueProvider).getNomenclatureValue(id);
        }

        Mockito.verify(questionnaireModelValueProvider, Mockito.times(questionnaireModelIds.size())).
                getQuestionnaireModelValue(Mockito.any(CollectionPlatform.class), Mockito.anyString());

        for (String id : questionnaireModelIds) {
            Mockito.verify(questionnaireModelValueProvider).getQuestionnaireModelValue(CollectionPlatform.coleman, id);
        }

        Mockito.verify(restTemplate, Mockito.times(allRequiredNomenclaturesIds.size())).exchange(
                Mockito.eq(colemanQuestionnaireUri + "/nomenclature"),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.eq(Nomenclature.class));

        for (Nomenclature nomenclature : nomenclatures) {
            Mockito.verify(restTemplate).exchange(
                    colemanQuestionnaireUri + "/nomenclature",
                    HttpMethod.POST,
                    new HttpEntity<>(nomenclature, headers),
                    Nomenclature.class);
        }

        Mockito.verify(restTemplate, Mockito.times(questionnaireModelIds.size())).exchange(
                Mockito.eq(colemanQuestionnaireUri + "/questionnaire-models"),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.eq(QuestionnaireModel.class));

        for (QuestionnaireModel model : models) {
            Mockito.verify(restTemplate).exchange(
                    colemanQuestionnaireUri + "/questionnaire-models",
                    HttpMethod.POST,
                    new HttpEntity<>(model, headers),
                    QuestionnaireModel.class);
        }
    }
}
