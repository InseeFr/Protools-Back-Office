package fr.insee.protools.backend.flowable.tasks.bpmn;

import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;

import java.util.List;
import java.util.Map;

public abstract class AbstractProcessTask
        implements JavaDelegate {
    public void execute(DelegateExecution delegateExecution) {
        String authenticatedUserId = Authentication.getAuthenticatedUserId();
        if (authenticatedUserId == null) {
            setAuthenticatedUser(delegateExecution);
        }

        try {
            Map<String, List<ExtensionElement>> extensionElements = getExtensionElements(delegateExecution);
            executeTask(delegateExecution, extensionElements);
        } finally {
            if (authenticatedUserId == null) {
                Authentication.setAuthenticatedUserId(null);
            }
        }
    }


    public abstract void executeTask(DelegateExecution paramDelegateExecution, Map<String, List<ExtensionElement>> paramMap);


    protected Map<String, List<ExtensionElement>> getExtensionElements(DelegateExecution delegateExecution) {
        FlowElement flowElement = delegateExecution.getCurrentFlowElement();
        return flowElement.getExtensionElements();
    }

    protected String getStringExtensionElementValue(String name, Map<String, List<ExtensionElement>> extensionElements, DelegateExecution delegateExecution, String defaultValue) {
        return getExtensionElementValue(name, extensionElements, delegateExecution, defaultValue);
    }

    protected <T> T getExtensionElementValue(String name, Map<String, List<ExtensionElement>> extensionElements, DelegateExecution delegateExecution, T defaultValue) {
        String str = null;
        T value = defaultValue;
        boolean exists = extensionElements.containsKey(name);
        if (!exists) {
            name = name.toLowerCase();
            exists = extensionElements.containsKey(name);
        }

        if (exists) {
            List<ExtensionElement> extensionElementValueList = extensionElements.get(name);
            if (extensionElementValueList != null && !extensionElementValueList.isEmpty()) {
                String valueText = extensionElementValueList.get(0).getElementText();
                if ((valueText != null && valueText.contains("${")) || valueText.contains("#{")) {
                    ExpressionManager expressionManager = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager();
                    Expression expression = expressionManager.createExpression(valueText);
                    value = (T) expression.getValue(delegateExecution);
                } else {
                    str = valueText;
                }
            }
        }

        return (T) str;
    }

    protected Boolean getBooleanExtensionElementValue(String name, Map<String, List<ExtensionElement>> extensionElements, DelegateExecution delegateExecution, Boolean defaultValue) {
        String value = getStringExtensionElementValue(name, extensionElements, delegateExecution, null);
        if (value == null) {
            return defaultValue;
        }

        return Boolean.valueOf(value);
    }

    protected void setAuthenticatedUser(DelegateExecution delegateExecution) {
        ExecutionEntity processInstance = CommandContextUtil.getExecutionEntityManager().findById(delegateExecution.getProcessInstanceId());

        Authentication.setAuthenticatedUserId(processInstance.getStartUserId());
    }
}
