package fr.insee.protools.backend.flowable.types;

import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.variable.api.types.ValueFields;
import org.flowable.variable.api.types.VariableType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ListLong implements VariableType {

    public static final String TYPE_NAME = "listLong";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public boolean isAbleToStore(Object value) {
        if (value == null) {
            return true;
        }
        return List.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Object getValue(ValueFields valueFields) {
        String valStr = valueFields.getTextValue();
        if (valStr != null) {
            valStr=valStr.replaceAll("\\s+", "");
            try {
                return Arrays.stream(valStr.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                throw new FlowableIllegalArgumentException("The given variable value is not comma separated list of Long: '" + valStr+ "'", e);
            }
        }
        return null;
    }


    @Override
    public void setValue(Object value, ValueFields valueFields) {
        try {
            if (value != null && value.getClass().isInstance(List.class)) {
                String listAsString = ((List<Long>) value).stream()
                        .map(Object::toString) // Map Long to String
                        .collect(Collectors.joining(","));
                valueFields.setTextValue(listAsString);
            } else {
                valueFields.setTextValue(null);
            }
        }
        catch (Exception e){
            valueFields.setTextValue(null);
        }
    }
}
