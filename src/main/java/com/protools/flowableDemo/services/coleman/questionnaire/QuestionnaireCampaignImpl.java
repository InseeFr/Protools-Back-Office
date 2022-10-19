package com.protools.flowableDemo.services.coleman.questionnaire;

import com.protools.flowableDemo.enums.CollectionPlatform;
import com.protools.flowableDemo.services.authentification.KeycloakService;
import com.protools.flowableDemo.services.providers.NomenclatureValueProvider;
import com.protools.flowableDemo.services.providers.QuestionnaireModelValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class QuestionnaireCampaignImpl implements QuestionnaireCampaign {

    @Value("${fr.insee.coleman.questionnaire.uri:#{null}}")
    private String colemanQuestionnaireUri;

    @Autowired
    private NomenclatureValueProvider nomenclatureValueProvider;

    @Autowired
    private QuestionnaireModelValueProvider questionnaireModelValueProvider;

    @Autowired
    private KeycloakService keycloakService;

    @Override
    public void createContext(
            QuestionnaireCampaignContext context,
            Map<String, String> nomenclatureLabelsMappedById,
            Map<String, String>questionnaireModelLabelsMappedById,
            Map<String, Collection<String>> requiredNomenclatureIdsMappedByQuestionnaireModelId) throws Exception {

        Collection<String> questionnaireModelIds = context.getQuestionnaireModelIds();

        for (String id : getAllRequiredNomenclaturesIds(
                questionnaireModelIds, requiredNomenclatureIdsMappedByQuestionnaireModelId)) {

            String label = nomenclatureLabelsMappedById.get(id);
            Collection<?> value = nomenclatureValueProvider.getNomenclatureValue(id);

            postNomenclature(new Nomenclature(id, label, value));
        }

        for (String id : questionnaireModelIds) {

            String label = questionnaireModelLabelsMappedById.get(id);
            Collection<String> requiredNomenclatureIds = requiredNomenclatureIdsMappedByQuestionnaireModelId.get(id);
            Map<?, ?> value = questionnaireModelValueProvider.getQuestionnaireModelValue(
                    CollectionPlatform.COLEMAN, id);

            postQuestionnaireModel(new QuestionnaireModel(id, label, requiredNomenclatureIds, value));
        }

        postCampaignContext(context);

    }

    private Collection<String> getAllRequiredNomenclaturesIds(
            Collection<String> questionnaireModelIds,
            Map<String, Collection<String>> requiredNomenclatureIdsMappedByQuestionnaireModelId) {

        Set<String> allRequiredNomenclaturesIds = new HashSet<>();

        for (String id : questionnaireModelIds) {
            Collection<String> questionnaireModelRequiredNomenclatureIds =
                    requiredNomenclatureIdsMappedByQuestionnaireModelId.getOrDefault(id, new ArrayList<>());

            allRequiredNomenclaturesIds.addAll(questionnaireModelRequiredNomenclatureIds);
        }

        return allRequiredNomenclaturesIds;
    }

    private void postNomenclature(Nomenclature nomenclature) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(keycloakService.getContextReferentialToken());

        HttpEntity<Nomenclature> request = new HttpEntity<>(nomenclature, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Nomenclature> response = restTemplate.exchange(
                colemanQuestionnaireUri + "/nomenclature", HttpMethod.POST, request, Nomenclature.class);
    }

    private void postQuestionnaireModel(QuestionnaireModel questionnaireModel) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(keycloakService.getContextReferentialToken());

        HttpEntity<QuestionnaireModel> request = new HttpEntity<>(questionnaireModel, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<QuestionnaireModel> response = restTemplate.exchange(
                colemanQuestionnaireUri + "/questionnaire-models", HttpMethod.POST, request,
                QuestionnaireModel.class);
    }

    private void postCampaignContext(QuestionnaireCampaignContext context) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(keycloakService.getContextReferentialToken());

        HttpEntity<QuestionnaireCampaignContext> request = new HttpEntity<>(context, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<QuestionnaireCampaignContext> response = restTemplate.exchange(
                colemanQuestionnaireUri + "/campaigns", HttpMethod.POST, request, QuestionnaireCampaignContext.class);
    }

}
