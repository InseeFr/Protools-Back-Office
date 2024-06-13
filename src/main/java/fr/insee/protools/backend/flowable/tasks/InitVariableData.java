package fr.insee.protools.backend.flowable.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.style.ToStringCreator;


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
        this.target = getNodeTextValue(jsonNode.path("target"));
        if (StringUtils.isEmpty(this.target)) {
            this.target = defaultValues.getTarget();
        }

        this.variableNameExpression = getNodeTextValue(jsonNode.path("name"));
        if (StringUtils.isEmpty(this.variableNameExpression)) {
            this.variableNameExpression = defaultValues.getVariableNameExpression();
        }

        this.variableValueExpression = getNodeTextValue(jsonNode.path("value"));
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


    public String toString() {
        return (new ToStringCreator(this))
                .append("target", this.target)
                .append("variableNameExpression", this.variableNameExpression)
                .append("variableValueExpression", this.variableValueExpression)
                .toString();
    }
}
