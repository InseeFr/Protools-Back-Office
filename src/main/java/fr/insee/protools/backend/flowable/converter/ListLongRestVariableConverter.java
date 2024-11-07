package fr.insee.protools.backend.flowable.converter;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.rest.variable.EngineRestVariable;
import org.flowable.common.rest.variable.RestVariableConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListLongRestVariableConverter implements RestVariableConverter {

    @Override
    public String getRestTypeName() {
        return "listLong";
    }

    @Override
    public Class<?> getVariableType() {
        return List.class;
    }

    @Override
    public Object getVariableValue(EngineRestVariable result) {
        if (result.getValue() != null) {
            if (!(result.getValue() instanceof String val)) {
                throw new FlowableIllegalArgumentException("Converter can only convert strings");
            }
            val = val.replaceAll("\\s+", "");
            try {
                return Arrays.stream(val.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                throw new FlowableIllegalArgumentException("The given variable value is not comma separated list of Long: '" + result.getValue() + "'", e);
            }
        }
        return null;
    }

    @Override
    public void convertVariableValue(Object variableValue, EngineRestVariable result) {
        if (variableValue != null) {
            if (!(variableValue instanceof List<?> list)) {
                throw new FlowableIllegalArgumentException("Converter can only convert list");
            }

            if (list.isEmpty()) {
                result.setValue("");
            } else if (list.get(0) instanceof Long) {
                String listAsString = list.stream()
                        .map(Object::toString) // Map Long to String
                        .collect(Collectors.joining(","));
                result.setValue(listAsString);
            } else {
                throw new FlowableIllegalArgumentException("Converter can only convert list of long");
            }
        } else {
            result.setValue(null);
        }
    }

}
