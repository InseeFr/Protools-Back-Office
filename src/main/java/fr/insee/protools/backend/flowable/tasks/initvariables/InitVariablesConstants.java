package fr.insee.protools.backend.flowable.tasks.initvariables;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class InitVariablesConstants {
    public static final String DELEGATE_EXPRESSION = "${initVariablesService}";
    public static final String VARIABLE_MAPPING = "variableMapping";
    public static final String ATTRIBUTE_TARGET = "target";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_VALUE = "value";
    public static final String ATTRIBUTE_VALUE_EXPRESSION = "valueExpression";
    public static final String ATTRIBUTE_VALUE_TYPE = "valueType";
    public static final String VARIABLE_ITEMS="items";
    public static final String VALUE_TYPE_JSON_OBJECT = "jsonObject";
    public static final String VALUE_TYPE_JSON_ARRAY = "jsonArray";
    public static final String VALUE_OVERWRITE = "overwrite";
    public static final Set<String> KNOWN_VALUE_TYPES = new HashSet<>(
            Arrays.asList(new String[]{"integer", "long", "double", "localDate", "string", "boolean", "variable", "jsonObject", "jsonArray"}));

}
