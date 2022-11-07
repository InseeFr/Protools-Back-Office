package com.protools.flowableDemo.services.coleman.questionnaire;

import com.protools.flowableDemo.enums.CollectionPlatform;
import com.protools.flowableDemo.services.authentification.KeycloakService;
import com.protools.flowableDemo.services.providers.NomenclatureValueProvider;
import com.protools.flowableDemo.services.providers.QuestionnaireModelValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class NomenclaturesAndQuestionnaireModelsIntegratorImpl implements
        NomenclaturesAndQuestionnaireModelsIntegrator {

    @Value("${fr.insee.coleman.questionnaire.uri:#{null}}")
    private String colemanQuestionnaireUri;

    @Autowired
    private NomenclatureValueProvider nomenclatureValueProvider;

    @Autowired
    private QuestionnaireModelValueProvider questionnaireModelValueProvider;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void execute(
            Collection<String> questionnaireModelIds,
            Map<String, String> questionnaireModelLabelsMappedById,
            Map<String, Collection<String>> requiredNomenclatureIdsMappedByQuestionnaireModelId,
            Map<String, String> nomenclatureLabelsMappedById) throws Exception {

        for (String id : getAllRequiredNomenclaturesIds(
                questionnaireModelIds, requiredNomenclatureIdsMappedByQuestionnaireModelId)) {

            String label = nomenclatureLabelsMappedById.get(id);
            Collection<?> value = nomenclatureValueProvider.getNomenclatureValue(id);

            postNomenclature(new Nomenclature(id, label, value));
        }

        for (String id : questionnaireModelIds) {
            String label = questionnaireModelLabelsMappedById.get(id);
            Collection<String> requiredNomenclatureIds =
                    requiredNomenclatureIdsMappedByQuestionnaireModelId.getOrDefault(id, List.of());
            Map<?, ?> value = questionnaireModelValueProvider.getQuestionnaireModelValue(
                    CollectionPlatform.coleman, id);

            postQuestionnaireModel(new QuestionnaireModel(id, label, requiredNomenclatureIds, value));
        }
    }

    private Collection<String> getAllRequiredNomenclaturesIds(
            Collection<String> questionnaireModelIds,
            Map<String, Collection<String>> requiredNomenclatureIdsMappedByQuestionnaireModelId) {

        Set<String> allRequiredNomenclaturesIds = new HashSet<>();

        for (String id : questionnaireModelIds) {
            Collection<String> questionnaireModelRequiredNomenclatureIds =
                    requiredNomenclatureIdsMappedByQuestionnaireModelId.getOrDefault(id, List.of());

            allRequiredNomenclaturesIds.addAll(questionnaireModelRequiredNomenclatureIds);
        }

        return allRequiredNomenclaturesIds;
    }

    private void postNomenclature(Nomenclature nomenclature) throws Exception {
        String uri = colemanQuestionnaireUri + "/nomenclature";

        HttpEntity<Nomenclature> request = new HttpEntity<>(nomenclature, getHeadersWithBearerAuth());

        restTemplate.exchange(uri, HttpMethod.POST, request, Nomenclature.class);
    }

    private void postQuestionnaireModel(QuestionnaireModel questionnaireModel) throws Exception {
        String uri = colemanQuestionnaireUri + "/questionnaire-models";

        HttpEntity<QuestionnaireModel> request = new HttpEntity<>(questionnaireModel, getHeadersWithBearerAuth());

        restTemplate.exchange(uri, HttpMethod.POST, request, QuestionnaireModel.class);
    }

    private HttpHeaders getHeadersWithBearerAuth() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(keycloakService.getContextReferentialToken());

        return headers;
    }
}
