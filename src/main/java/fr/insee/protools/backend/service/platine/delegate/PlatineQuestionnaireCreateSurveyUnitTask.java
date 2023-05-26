package fr.insee.protools.backend.service.platine.delegate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.protools.backend.service.DelegateContextVerifier;
import fr.insee.protools.backend.service.common.platine_sabiane.dto.surveyunit.SurveyUnitResponseDto;
import fr.insee.protools.backend.service.context.ContextService;
import fr.insee.protools.backend.service.exception.IncorrectSUException;
import fr.insee.protools.backend.service.platine.questionnaire.PlatineQuestionnaireService;
import fr.insee.protools.backend.service.rem.dto.REMSurveyUnitDto;
import fr.insee.protools.backend.service.utils.FlowableVariableUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_CURRENT_PARTITION_ID;
import static fr.insee.protools.backend.service.FlowableVariableNameConstants.VARNAME_REM_SURVEY_UNIT;
import static fr.insee.protools.backend.service.context.ContextConstants.*;
import static fr.insee.protools.backend.service.utils.ContextUtils.getCurrentPartitionNode;

@Slf4j
@Component
public class PlatineQuestionnaireCreateSurveyUnitTask implements JavaDelegate, DelegateContextVerifier {


    @Autowired ContextService protoolsContext;
    @Autowired PlatineQuestionnaireService platineQuestionnaireService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void execute(DelegateExecution execution) {
        log.info("ProcessInstanceId={}  begin", execution.getProcessInstanceId());
        JsonNode contextRootNode = protoolsContext.getContextByProcessInstance(execution.getProcessInstanceId());

        String currentPartitionId = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_CURRENT_PARTITION_ID, String.class);
        JsonNode remSUNode = FlowableVariableUtils.getVariableOrThrow(execution,VARNAME_REM_SURVEY_UNIT, JsonNode.class);
        JsonNode currentPartitionNode = getCurrentPartitionNode(contextRootNode, currentPartitionId);

        //Create the platine DTO object
        SurveyUnitResponseDto dto = computeDto(remSUNode,currentPartitionNode);
        //Call service
        platineQuestionnaireService.postSurveyUnit(dto, contextRootNode.path(CTX_CAMPAGNE_ID).asText());

        log.info("ProcessInstanceId={}  end",execution.getProcessInstanceId());
    }

    private SurveyUnitResponseDto computeDto(JsonNode remSUNode, JsonNode currentPartitionNode) {
        REMSurveyUnitDto remSurveyUnitDto;
        try {
            remSurveyUnitDto = objectMapper.treeToValue(remSUNode, REMSurveyUnitDto.class);
        } catch (JsonProcessingException e) {
            throw new IncorrectSUException("Error while parsing the json retrieved from REM : " + VARNAME_REM_SURVEY_UNIT,remSUNode, e);
        }

        ArrayNode personalizationNode = objectMapper.createArrayNode();
        personalizationNode.add(objectMapper.createObjectNode()
                .put("name","whoAnswers1")
                .put("value",currentPartitionNode.path(CTX_PARTITION_QUIREPOND1).asText()));
        personalizationNode.add(objectMapper.createObjectNode()
                .put("name","whoAnswers2")
                .put("value",currentPartitionNode.path(CTX_PARTITION_QUIREPOND2).asText()));
        personalizationNode.add(objectMapper.createObjectNode()
                .put("name","whoAnswers3")
                .put("value",currentPartitionNode.path(CTX_PARTITION_QUIREPOND3).asText()));

        return SurveyUnitResponseDto.builder()
                .id(remSurveyUnitDto.getRepositoryId().toString())
                .questionnaireId(currentPartitionNode.path(CTX_PARTITION_QUESTIONNAIRE_MODEL).asText())
                .data(objectMapper.createObjectNode())
                .personalization(personalizationNode)
                .comment(objectMapper.createObjectNode())
                .stateData(objectMapper.createObjectNode())
                .build();
    }

    @Override
    public Set<String> getContextErrors(JsonNode contextRootNode) {
        if(contextRootNode==null){
            return Set.of("Context is missing");
        }
        Set<String> results=new HashSet<>();
        Set<String> requiredNodes =
                Set.of(
                        //Global & Campaign
                        CTX_PARTITIONS
                );
        Set<String> requiredPartition =
                Set.of(CTX_PARTITION_ID,CTX_PARTITION_QUESTIONNAIRE_MODEL,CTX_PARTITION_QUIREPOND1,CTX_PARTITION_QUIREPOND2,CTX_PARTITION_QUIREPOND3);

        results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredNodes,contextRootNode,getClass()));

        //Maybe one day we will have partitions for platine and partitions for sabiane and we will only validate the platine ones
        //Partitions
        var partitionIterator =contextRootNode.path(CTX_PARTITIONS).elements();
        while (partitionIterator.hasNext()) {
            var partitionNode = partitionIterator.next();
            results.addAll(DelegateContextVerifier.computeMissingChildrenMessages(requiredPartition,partitionNode,getClass()));
        }
        return results;
    }

}
