package com.protools.flowableDemo.services.coleman;

import com.protools.flowableDemo.dto.Nomenclature;
import com.protools.flowableDemo.dto.QuestionnaireModel;
import com.protools.flowableDemo.enums.CollectionPlatform;
import com.protools.flowableDemo.services.authentification.KeycloakService;
import com.protools.flowableDemo.services.providers.NomenclatureValueProvider;
import com.protools.flowableDemo.services.providers.QuestionnaireModelValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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
    public void execute(Collection<QuestionnaireModel> models,
                        Collection<Nomenclature> nomenclatures) throws Exception {

        Map<String, String> nomenclatureLabelsMappedById = getNomenclatureLabelsMappedById(nomenclatures);

        for (String id : getAllRequiredNomenclaturesIds(models)) {
            String label = nomenclatureLabelsMappedById.get(id);
            Collection<?> value = nomenclatureValueProvider.getNomenclatureValue(id);

            postNomenclature(new Nomenclature(id, label, value));
        }

        for (QuestionnaireModel model : models) {
            String id = model.getId();
            String label = model.getLabel();
            Collection<String> requiredNomenclatureIds = model.getRequiredNomenclatureIds();
            Map<?, ?> value = questionnaireModelValueProvider.getQuestionnaireModelValue(
                    CollectionPlatform.coleman, model.getId());

            postQuestionnaireModel(new QuestionnaireModel(id, label, requiredNomenclatureIds, value));
        }
    }

    private Collection<String> getAllRequiredNomenclaturesIds(Collection<QuestionnaireModel> models) {
        Set<String> allRequiredNomenclaturesIds = new HashSet<>();

        for (QuestionnaireModel model : models) {
            allRequiredNomenclaturesIds.addAll(model.getRequiredNomenclatureIds());
        }

        return allRequiredNomenclaturesIds;
    }

    private Map<String, String> getNomenclatureLabelsMappedById(Collection<Nomenclature> nomenclatures) {
        return nomenclatures.stream().collect(Collectors.toMap(Nomenclature::getId, Nomenclature::getLabel));
    }

    private void postNomenclature(Nomenclature nomenclature) throws Exception {
        String uri = colemanQuestionnaireUri + "/nomenclature";

        HttpEntity<Nomenclature> request = new HttpEntity<>(nomenclature, getHeadersWithBearerAuthAndContentType());

        restTemplate.exchange(uri, HttpMethod.POST, request, Nomenclature.class);
    }

    private void postQuestionnaireModel(QuestionnaireModel questionnaireModel) throws Exception {
        String uri = colemanQuestionnaireUri + "/questionnaire-models";

        HttpEntity<QuestionnaireModel> request = new HttpEntity<>(
                questionnaireModel, getHeadersWithBearerAuthAndContentType());

        restTemplate.exchange(uri, HttpMethod.POST, request, QuestionnaireModel.class);
    }

    private HttpHeaders getHeadersWithBearerAuthAndContentType() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(keycloakService.getContextReferentialToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

}
