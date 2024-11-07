package fr.insee.protools.backend.service.utils;

import fr.insee.protools.backend.service.exception.VariableClassCastException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.delegate.DelegateExecution;

public class FlowableVariableUtils {

    private FlowableVariableUtils() {
    }

    /**
     * Helper to call VariableScopeImpl#getVariable(String,Class<T>).
     * Throws Protools VariableClassCastException if the cast is not successful
     * Throws FlowableIllegalArgumentException if the variable is not found
     */
    @SuppressWarnings("squid:S1192")
    public static <T> T getVariableOrThrow(DelegateExecution execution, String variableName, Class<T> variableClass) {
        T res;
        try {
            res = execution.getVariable(variableName, variableClass);
            if (res == null) {
                throw new FlowableIllegalArgumentException(getMissingVariableMessage(variableName));
            } else if (!variableClass.isAssignableFrom(res.getClass())) {
                throw new VariableClassCastException(String.format("Variable ID=[%s] val=[%s] cannot be casted to %s", variableName, res, variableClass));
            }
        } catch (ClassCastException e) {
            throw new VariableClassCastException(String.format("Variable ID=[%s] val=[%s] cannot be casted to %s", variableName, execution.getVariable(variableName), variableClass));
        }
        return res;
    }

    public static String getMissingVariableMessage(String variableName) {
        return "Variable '" + variableName + "' was not found";
    }

    /**
     * Helper to call VariableScopeImpl#getVariable(String,Class<T>).
     * Throws Protools VariableClassCastException if the cast is not successful
     * return null if the variable is not defined
     */
    public static <T> T getVariableOrNull(DelegateExecution execution, String variableName, Class<T> variableClass) {
        try {
            return execution.getVariable(variableName, variableClass);
        } catch (ClassCastException e) {
            throw new VariableClassCastException(String.format("Variable ID=[%s] val=[%s] cannot be casted to %s", variableName, execution.getVariable(variableName), variableClass));
        }
    }
}
