package fr.insee.protools.backend.flowable.tasks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.common.engine.impl.context.Context;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.variable.MapDelegateVariableContainer;
import org.flowable.variable.api.delegate.VariableScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
public class InitVariablesService
        extends AbstractPlatformTask {
    private static final String OVERWRITE = "overwrite";
    private static final String VARIABLE_MAPPING = "variableMapping";
    private static final String ATTRIBUTE_TARGET = "target";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_VALUE_TYPE = "valueType";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_VALUE_EXPRESSION = "valueExpression";
    private static final String VALUE_TYPE_INTEGER = "integer";
    private static final String VALUE_TYPE_LONG = "long";
    private static final String VALUE_TYPE_DOUBLE = "double";
    private static final String VALUE_TYPE_LOCAL_DATE = "localDate";
    private static final String VALUE_TYPE_STRING = "string";
    private static final String VALUE_TYPE_BOOLEAN = "boolean";
    private static final String VALUE_TYPE_VARIABLE = "variable";
    private static final String VALUE_TYPE_JSON_OBJECT = "jsonObject";
    private static final String VALUE_TYPE_JSON_ARRAY = "jsonArray";
    private static final Logger LOGGER = LoggerFactory.getLogger(InitVariablesService.class);

    protected ObjectMapper objectMapper;

    public InitVariablesService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void executeTask(VariableContainer variableContainer, ExtensionElementsContainer extensionElementsContainer) {
        Object booleanValue = getExtensionElementValue("overwrite", extensionElementsContainer, variableContainer, null);
        boolean overwrite = getBooleanValue(booleanValue);

        Stream<MappingInfo> variableMapping = extensionElementsContainer.getExtensionElements("variableMapping").map(extensionElement -> toMappingInfo(extensionElement));
        ExpressionManager expressionManager = getExpressionManager(variableContainer);
        IsolatedMapDelegateVariableContainer isolatedVariableContainer = null;
        Iterator<MappingInfo> iterator = variableMapping.iterator();
        while (iterator.hasNext()) {
            MappingInfo mappingInfo = iterator.next();
            if (mappingInfo.getMappingType() == MappingInfo.MappingType.DEFAULT) {
                processInitVariable(expressionManager, mappingInfo, variableContainer, overwrite);
                continue;
            }
            if (mappingInfo.getMappingType() == MappingInfo.MappingType.ISOLATED) {
                if (isolatedVariableContainer == null) {
                    isolatedVariableContainer = new IsolatedMapDelegateVariableContainer(variableContainer);
                }
                processInitVariable(expressionManager, mappingInfo, (VariableContainer) isolatedVariableContainer, overwrite);
            }
        }
        if (isolatedVariableContainer != null) {
            for (Map.Entry<String, Object> variable : (Iterable<Map.Entry<String, Object>>) isolatedVariableContainer.getTransientVariables().entrySet()) {
                variableContainer.setVariable(variable.getKey(), variable.getValue());
            }
        }
    }

    protected MappingInfo toMappingInfo(BaseExtensionElement mappingElement) {
        MappingInfo mappingInfo = new MappingInfo(mappingElement);
        mappingInfo.setMappingType(MappingInfo.MappingType.DEFAULT);
        return mappingInfo;
    }


    protected void processInitVariable(ExpressionManager expressionManager, MappingInfo mappingInfo, VariableContainer variableContainer, boolean overwrite) {
        String target = (mappingInfo.getTarget() != null) ? mappingInfo.getTarget() : "self";
        String name = mappingInfo.getName();
        String variableName = getVariableName(expressionManager, target, name, variableContainer);
        Expression variableExpression = expressionManager.createExpression(variableName);
        if (overwrite || variableDoesNotExistInScope(variableExpression, variableContainer)) {
            String valueType = mappingInfo.getValueType();
            String value = mappingInfo.getValue();
            String valueExpression = mappingInfo.getValueExpression();
            Object variableValue = getVariableValue(expressionManager, valueType, value, valueExpression, variableContainer);
            variableExpression.setValue(variableValue, variableContainer);
        }
    }


    @Deprecated
    public void initVariables(ExpressionManager expressionManager, VariableScope variableScope, String initVariablesMap, Expression overwriteExpression) {
        try {
            boolean overwrite = getBooleanValue(overwriteExpression, variableScope);
            parseInitVariableMap(initVariablesMap).forEach(initVariableData -> {
                LOGGER.debug("Processing init variable data '{}'", initVariableData);

                String variableName = getVariableName(expressionManager, initVariableData.getTarget(), initVariableData.getVariableNameExpression(), (VariableContainer) variableScope);

                Expression variableExpression = expressionManager.createExpression(variableName);

                if (overwrite || variableDoesNotExistInScope(variableExpression, (VariableContainer) variableScope)) {
                    String variableValueExpression = initVariableData.getVariableValueExpression();

                    Object variableValue = StringUtils.isNotEmpty(variableValueExpression) ? expressionManager.createExpression(variableValueExpression).getValue((VariableContainer) variableScope) : null;
                    variableExpression.setValue(variableValue, (VariableContainer) variableScope);
                }
            });
        } catch (Exception e) {
            throw new FlowableException("Unable to initialize variables", e);
        }
    }

    protected boolean getBooleanValue(Expression expression, VariableScope variableScope) {
        if (expression == null) {
            return false;
        }

        Object value = expression.getValue((VariableContainer) variableScope);
        return getBooleanValue(value);
    }

    private boolean getBooleanValue(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        return (value != null && Boolean.valueOf(value.toString()).booleanValue());
    }


    protected boolean variableDoesNotExistInScope(Expression variableExpression, VariableContainer variableContainer) {
        Object value = null;
        try {
            value = variableExpression.getValue(variableContainer);
        } catch (FlowableException e) {
            return e.getCause() instanceof org.flowable.common.engine.impl.javax.el.PropertyNotFoundException;
        }
        return (value == null);
    }

    protected Object getVariableValue(ExpressionManager expressionManager, String variableValueType, String variableValue, String variableValueExpression, VariableContainer variableContainer) {
        if (StringUtils.isNotEmpty(variableValueExpression)) {
            return expressionManager.createExpression(variableValueExpression).getValue(variableContainer);
        }
        return getVariableValue(expressionManager, variableValueType, variableValue, variableContainer);
    }

    private Object getVariableValue(ExpressionManager expressionManager, String variableValueType, String variableValue, VariableContainer variableContainer) {
        ArrayNode arrayNode;
        if (StringUtils.isEmpty(variableValueType)) {

            return StringUtils.isNotEmpty(variableValue) ?
                    expressionManager.createExpression(variableValue).getValue(variableContainer) : null;
        }

        switch (variableValueType) {
            case "string":
                return parseValueIfNotEmpty(variableValue, (Function) Function.identity());
            case "integer":
                return parseValueIfNotEmpty(variableValue, Integer::parseInt);
            case "long":
                return parseValueIfNotEmpty(variableValue, Long::parseLong);
            case "double":
                return parseValueIfNotEmpty(variableValue, Double::parseDouble);
            case "localDate":
                return parseValueIfNotEmpty(variableValue, this::parseLocalDate);
            case "boolean":
                return parseValueIfNotEmpty(variableValue, Boolean::parseBoolean);
            case "variable":
                return StringUtils.isNotEmpty(variableValue) ?
                        expressionManager.createExpression("${" + variableValue + "}").getValue(variableContainer) : null;
            case "jsonObject":
                return this.objectMapper.createObjectNode();
            case "jsonArray":
                arrayNode = this.objectMapper.createArrayNode();
                if (StringUtils.isNotEmpty(variableValue)) {
                    int arrayNodeSize = Integer.parseInt(variableValue);
                    while (arrayNode.size() < arrayNodeSize) {
                        arrayNode.addObject();
                    }
                }
                return arrayNode;
        }
        throw new FlowableIllegalArgumentException("Unknown variable value type " + variableValueType);
    }


    protected Object parseLocalDate(String localDate) {
        return LocalDate.parse(localDate).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
    }

    protected <T> T parseValueIfNotEmpty(String value, Function<String, T> valueParser) {
        return StringUtils.isNotEmpty(value) ? valueParser.apply(value) : null;
    }


    protected String getVariableName(ExpressionManager expressionManager, String target, String name, VariableContainer variableContainer) {
        return "${" + (String) StringUtils.defaultIfEmpty(target, "self") + "." + expressionManager

                .createExpression(name).getValue(variableContainer) + "}";
    }


    protected List<InitVariableData> parseInitVariableMap(String variableMapString) throws IOException {
        InitVariableData defaultValues = new InitVariableData("self", null, null);
        List<InitVariableData> initVariablesList = new ArrayList<>();

        if (StringUtils.isNotEmpty(variableMapString)) {
            JsonNode expressionNode = parseJson(variableMapString);
            JsonNode variableItems = expressionNode.get("items");
            if (variableItems != null) {
                for (JsonNode variableItem : variableItems) {
                    InitVariableData initVariableData = new InitVariableData(variableItem, defaultValues);
                    initVariablesList.add(initVariableData);
                }

            } else if (expressionNode.isArray()) {
                for (JsonNode variableItem : expressionNode) {
                    InitVariableData initVariableData = new InitVariableData(variableItem, defaultValues);
                    initVariablesList.add(initVariableData);
                }
            }
        }
        return initVariablesList;
    }

    protected JsonNode parseJson(String json) throws IOException {
        return this.objectMapper.readTree(json);
    }

    protected static class MappingInfo {
        BaseExtensionElement extensionElement;
        MappingType mappingType;

        public MappingInfo(BaseExtensionElement extensionElement) {
            this.extensionElement = extensionElement;
        }

        String getValueType() {
            return this.extensionElement.getAttributeValue("valueType").orElse(null);
        }

        String getValue() {
            return this.extensionElement.getAttributeValue("value").orElse(null);
        }

        String getValueExpression() {
            return this.extensionElement.getAttributeValue("valueExpression").orElse(null);
        }

        String getTarget() {
            return this.extensionElement.getAttributeValue("target").orElse(null);
        }

        String getName() {
            return this.extensionElement.getAttributeValue("name").orElse(null);
        }


        String getVariableName() {
            String target = getTarget();
            if (target != null) {
                int dotIndex = target.indexOf('.');
                if (dotIndex > 0) {
                    return target.substring(0, dotIndex);
                }
                return target;
            }
            return getName();
        }

        void setMappingType(MappingType mappingType) {
            this.mappingType = mappingType;
        }

        public MappingType getMappingType() {
            return this.mappingType;
        }


        enum MappingType {
            DEFAULT,
            ISOLATED;
        }
    }

    enum MappingType {DEFAULT, ISOLATED;}


    protected static class IsolatedMapDelegateVariableContainer
            extends MapDelegateVariableContainer {
        public IsolatedMapDelegateVariableContainer(VariableContainer delegate) {
            super(new LinkedHashMap<>(), delegate);
        }

        public void setVariable(String key, Object value) {
            setTransientVariable(key, value);
        }
    }
}
