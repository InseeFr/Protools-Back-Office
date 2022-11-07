package com.protools.flowableDemo.services.coleman.questionnaire;

import com.protools.flowableDemo.enums.CollectionPlatform;
import com.protools.flowableDemo.services.authentification.KeycloakService;
import com.protools.flowableDemo.services.providers.NomenclatureValueProvider;
import com.protools.flowableDemo.services.providers.QuestionnaireModelValueProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.*;

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

        Collection<String> questionnaireModelIds = List.of("model-1", "model-2", "model-3");

        Map<String, String>questionnaireModelLabelsMappedById = Map.of(
                "model-1", "label-model-1",
                "model-2", "label-model-2",
                "model-3", "label-model-3");

        Map<String, Collection<String>> requiredNomenclatureIdsMappedByQuestionnaireModelId = Map.of(
                "model-1", List.of("nomenclature-1", "nomenclature-2"),
                "model-3", List.of("nomenclature-2", "nomenclature-3"));

        Map<String, String> nomenclatureLabelsMappedById = Map.of(
                "nomenclature-1", "label-nomenclature-1",
                "nomenclature-2", "label-nomenclature-2",
                "nomenclature-3", "label-nomenclature-3");

        Collection<String> allRequiredNomenclaturesIds = new ArrayList<>(nomenclatureLabelsMappedById.keySet());

        String token = "token-value";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        Mockito
                .when(keycloakService.getContextReferentialToken())
                .thenReturn(token);

        integrator.execute(
                questionnaireModelIds,
                questionnaireModelLabelsMappedById,
                requiredNomenclatureIdsMappedByQuestionnaireModelId,
                nomenclatureLabelsMappedById);

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

        for (String id : allRequiredNomenclaturesIds) {
            Nomenclature nomenclature = new Nomenclature(id, nomenclatureLabelsMappedById.get(id), List.of());

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

        for (String id : questionnaireModelIds) {
            QuestionnaireModel questionnaireModel = new QuestionnaireModel(
                    id,  questionnaireModelLabelsMappedById.get(id),
                    requiredNomenclatureIdsMappedByQuestionnaireModelId.getOrDefault(id, List.of()), Map.of());

            Mockito.verify(restTemplate).exchange(
                    colemanQuestionnaireUri + "/questionnaire-models",
                    HttpMethod.POST,
                    new HttpEntity<>(questionnaireModel, headers),
                    QuestionnaireModel.class);
        }
    }
}
