package fr.insee.protools.backend.flowable.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.style.ToStringCreator;

@ToString
class InitVariableData {
    protected String target;
    protected String variableNameExpression;
    protected String variableValueExpression;

    protected static final String ATTRIBUTE_TARGET = "target";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_VALUE = "value";

    public InitVariableData(String target, String variableNameExpression, String variableValueExpression) {
        this.target = target;
        this.variableNameExpression = variableNameExpression;
        this.variableValueExpression = variableValueExpression;
    }


    public InitVariableData(JsonNode jsonNode, InitVariableData defaultValues) {
        this.target = getNodeTextValue(jsonNode.path(ATTRIBUTE_TARGET));
        if (StringUtils.isEmpty(this.target)) {
            this.target = defaultValues.getTarget();
        }

        this.variableNameExpression = getNodeTextValue(jsonNode.path(ATTRIBUTE_NAME));
        if (StringUtils.isEmpty(this.variableNameExpression)) {
            this.variableNameExpression = defaultValues.getVariableNameExpression();
        }

        this.variableValueExpression = getNodeTextValue(jsonNode.path(ATTRIBUTE_VALUE));
        if (StringUtils.isEmpty(this.variableValueExpression)) {
            this.variableValueExpression = defaultValues.getVariableValueExpression();
        }
    }

    protected String getNodeTextValue(JsonNode targetNode) {
        return targetNode.isNull() ? null : targetNode.asText();
    }

    public String getTarget() {
        return this.target;
    }

    public String getVariableNameExpression() {
        return this.variableNameExpression;
    }

    public String getVariableValueExpression() {
        return this.variableValueExpression;
    }

}
