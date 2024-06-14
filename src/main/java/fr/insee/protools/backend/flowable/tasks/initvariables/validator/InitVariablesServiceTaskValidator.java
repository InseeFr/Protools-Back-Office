package fr.insee.protools.backend.flowable.tasks.initvariables.validator;

import fr.insee.protools.backend.flowable.validator.PlatformServiceTaskValidator;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ValidatorImpl;

import java.util.*;

import static fr.insee.protools.backend.flowable.tasks.initvariables.InitVariablesConstants.*;


public class InitVariablesServiceTaskValidator
        extends ValidatorImpl
        implements PlatformServiceTaskValidator {

    public void validate(BpmnModel bpmnModel, List<ValidationError> list) {
        throw new UnsupportedOperationException("This validator can only be used as a " + PlatformServiceTaskValidator.class);
    }


    public String getDelegateExpression() {
        return DELEGATE_EXPRESSION;
    }


    public void validate(BpmnModel bpmnModel, Process process, ServiceTask serviceTask, List<ValidationError> errors) {
        List<ExtensionElement> variableMappings = (List<ExtensionElement>) serviceTask.getExtensionElements().getOrDefault(VARIABLE_MAPPING, Collections.emptyList());
        if (variableMappings.isEmpty()) {
            addWarning(errors, "init-variables-no-variable-mappings", process, (BaseElement) serviceTask, "There are no defined variable mappings");
        } else {
            for (ExtensionElement variableMapping : variableMappings) {
                validateVariableMapping(bpmnModel, process, serviceTask, variableMapping, errors);
            }
        }
    }


    protected void validateVariableMapping(BpmnModel bpmnModel, Process process, ServiceTask serviceTask, ExtensionElement variableMapping, List<ValidationError> errors) {
        String target = variableMapping.getAttributeValue(null, "target");
        String name = variableMapping.getAttributeValue(null, "name");
        if (StringUtils.isAllEmpty(new CharSequence[]{target, name})) {
            addError(errors, "init-variables-no-target-or-name", process, (FlowElement) serviceTask, (BaseElement) variableMapping, "At least one of 'target' or 'name' must be defined");
        }


        String valueType = variableMapping.getAttributeValue(null, ATTRIBUTE_VALUE_TYPE);
        if (StringUtils.isNotEmpty(valueType) && !KNOWN_VALUE_TYPES.contains(valueType)) {
            addWarning(errors, "init-variables-unknown-value-type", process, (FlowElement) serviceTask, (BaseElement) variableMapping, "Unknown value type");
        }

        String value = variableMapping.getAttributeValue(null, "value");
        String valueExpression = variableMapping.getAttributeValue(null, "valueExpression");
        if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(valueExpression)) {
            addWarning(errors, "init-variables-variable-value-and-value-expression", process, (FlowElement) serviceTask, (BaseElement) variableMapping, "Both 'value' and 'valueExpression' are defined. 'value' will be ignored");
        } else if (!"jsonObject".equals(valueType) && StringUtils.isAllEmpty(new CharSequence[]{value, valueExpression})) {
            addWarning(errors, "init-variables-no-value-or-value-expression", process, (FlowElement) serviceTask, (BaseElement) variableMapping, "At least one of 'value' or 'valueExpression' must be defined");
        }
    }
}