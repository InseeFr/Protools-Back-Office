package fr.insee.protools.backend.flowable.tasks.initvariables;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@ToString
@Getter
class InitVariableData {
    protected String target;
    protected String variableNameExpression;
    protected String variableValueExpression;

    public InitVariableData(String target, String variableNameExpression, String variableValueExpression) {
        this.target = target;
        this.variableNameExpression = variableNameExpression;
        this.variableValueExpression = variableValueExpression;
    }


    public InitVariableData(JsonNode jsonNode, InitVariableData defaultValues) {
        this.target = getNodeAsText(jsonNode.path(ATTRIBUTE_TARGET));
        if (StringUtils.isEmpty(this.target)) {
            this.target = defaultValues.getTarget();
        }

        this.variableNameExpression = getNodeAsText(jsonNode.path(ATTRIBUTE_NAME));
        if (StringUtils.isEmpty(this.variableNameExpression)) {
            this.variableNameExpression = defaultValues.getVariableNameExpression();
        }

        this.variableValueExpression = getNodeAsText(jsonNode.path(ATTRIBUTE_VALUE));
        if (StringUtils.isEmpty(this.variableValueExpression)) {
            this.variableValueExpression = defaultValues.getVariableValueExpression();
        }
    }

    protected static String getNodeAsText(JsonNode targetNode) {
        return targetNode.isNull() ? null : targetNode.asText();
    }
}
