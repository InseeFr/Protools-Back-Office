package fr.insee.protools.backend.flowable.tasks;

import fr.insee.protools.backend.flowable.tasks.bpmn.BpmnExtensionElementsContainer;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.util.CommandContextUtil;


public abstract class AbstractPlatformTask
        implements JavaDelegate {


    public void execute(DelegateExecution delegateExecution) {
        executeTask(delegateExecution, getExtensionElementsContainer(delegateExecution));
    }


    public abstract void executeTask(VariableContainer paramVariableContainer, ExtensionElementsContainer paramExtensionElementsContainer);

    protected ExtensionElementsContainer getExtensionElementsContainer(DelegateExecution delegateExecution) {
        return new BpmnExtensionElementsContainer(delegateExecution.getCurrentFlowElement());
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

    protected <T> T resolveValue(VariableContainer variableContainer, String valueText) {
        String str = null;
        if (valueText != null && (valueText.contains("${") || valueText.contains("#{"))) {
            Expression expression = getExpressionManager(variableContainer).createExpression(valueText);
            T value = (T) expression.getValue(variableContainer);
        } else {
            str = valueText;
        }
        return (T) str;
    }


    protected ExpressionManager getExpressionManager(VariableContainer variableContainer) {
        return CommandContextUtil.getProcessEngineConfiguration().getExpressionManager();
    }


}