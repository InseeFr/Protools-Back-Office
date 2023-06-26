package fr.insee.protools.backend.service.utils;

import fr.insee.protools.backend.service.exception.VariableClassCastException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.engine.delegate.DelegateExecution;

public class FlowableVariableUtils {

    /**
     * Helper to call VariableScopeImpl#getVariable(String,Class<T>).
     * Throws Protools VariableClassCastException if the cast is not successful
     * Throws FlowableIllegalArgumentException if the variable is not found
     */
    public static <T> T getVariableOrThrow(DelegateExecution execution, String variableName, Class<T> variableClass ){
        T res;
        try{
            res = execution.getVariable(variableName, variableClass);
            if(res==null){
                throw new FlowableIllegalArgumentException("Variable '" + variableName + "' was not found");
            }
        }
        catch (ClassCastException e) {
            throw new VariableClassCastException(String.format("Variable ID=[%s] val=[%s] cannot be casted to %s", variableName, execution.getVariable(variableName), variableClass));
        }
        return res;
    }


    private FlowableVariableUtils(){}
}
