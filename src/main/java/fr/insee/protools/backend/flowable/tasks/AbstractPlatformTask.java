package fr.insee.protools.backend.flowable.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import fr.insee.protools.backend.flowable.tasks.bpmn.BpmnExtensionElementsContainer;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;


public abstract class AbstractPlatformTask
        implements JavaDelegate
{
    protected static final Pattern TRIM_COMMA_SEPARATED = Pattern.compile("[\\s]*,[\\s]*");


    public void execute(DelegateExecution delegateExecution) {
        executeTask((VariableContainer)delegateExecution, getExtensionElementsContainer(delegateExecution));
    }


    public abstract void executeTask(VariableContainer paramVariableContainer, ExtensionElementsContainer paramExtensionElementsContainer);

    protected ExtensionElementsContainer getExtensionElementsContainer(DelegateExecution delegateExecution) {
        return (ExtensionElementsContainer)new BpmnExtensionElementsContainer((BaseElement)delegateExecution.getCurrentFlowElement());
    }

    protected String getStringExtensionElementValue(String name, ExtensionElementsContainer extensionElementsContainer, VariableContainer variableContainer, String defaultValue) {
        return getExtensionElementValue(name, extensionElementsContainer, variableContainer, defaultValue);
    }



    protected boolean getBooleanExtensionElementValue(String name, ExtensionElementsContainer extensionElementsContainer, VariableContainer variableContainer, boolean defaultValue) {
        String value = getExtensionElementValue(name, extensionElementsContainer, variableContainer, Boolean.toString(defaultValue));
        if (StringUtils.isNotEmpty(value)) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    protected Collection<String> asStringCollection(Object value, Supplier<String> errorMessageDetailsSupplier) {
        if (value == null)
            return null;
        if (value instanceof String)
            return Arrays.asList(TRIM_COMMA_SEPARATED.split((String)value));
        if (value instanceof Collection)
            return (Collection<String>)value;
        if (value instanceof ArrayNode) {
            ArrayNode arrayNode = (ArrayNode)value;
            List<String> values = new ArrayList<>(arrayNode.size());
            for (JsonNode node : arrayNode) {
                values.add(node.asText());
            }

            return values;
        }
        throw new FlowableIllegalArgumentException("Type of " + value.getClass().getCanonicalName() + " is not supported for " + (String)errorMessageDetailsSupplier.get());
    }



    protected <T> T getExtensionElementValue(String name, ExtensionElementsContainer extensionElementsContainer, VariableContainer variableContainer, T defaultValue) {
        T value = internalGetExtensionElementValue(name, extensionElementsContainer, variableContainer, defaultValue);


        if (value == null) {
            value = internalGetExtensionElementValue(name.toLowerCase(), extensionElementsContainer, variableContainer, defaultValue);
        }

        return value;
    }


    private <T> T internalGetExtensionElementValue(String name, ExtensionElementsContainer extensionElementsContainer, VariableContainer variableContainer, T defaultValue) {
        T value = defaultValue;
        BaseExtensionElement extensionElement = extensionElementsContainer.getExtensionElement(name).orElse(null);
        if (extensionElement != null) {
            String valueText = extensionElement.getText();
            value = resolveValue(variableContainer, valueText);
        }
        return value;
    }

    private <T> T internalGetExtensionElementAttributeValue(String attributeName, BaseExtensionElement extensionElements, VariableContainer variableContainer, T defaultValue) {
        T value = defaultValue;
        String attributeValue = extensionElements.getAttributeValue(attributeName).orElse(null);
        if (attributeValue != null) {
            value = resolveValue(variableContainer, attributeValue);
        }
        return value;
    }

    protected <T> T resolveValue(VariableContainer variableContainer, String valueText) {
        String str=null;
        if (valueText != null && (valueText.contains("${") || valueText.contains("#{"))) {
            Expression expression = getExpressionManager(variableContainer).createExpression(valueText);
            T value = (T)expression.getValue(variableContainer);
        } else {
            str = valueText;
        }
        return (T)str;
    }


    protected ExpressionManager getExpressionManager(VariableContainer variableContainer) {
        return CommandContextUtil.getProcessEngineConfiguration().getExpressionManager();
    }


}