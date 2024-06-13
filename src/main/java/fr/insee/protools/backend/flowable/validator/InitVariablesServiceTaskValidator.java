
package fr.insee.protools.backend.flowable.validator;
 
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 import org.apache.commons.lang3.StringUtils;
 import org.flowable.bpmn.model.BaseElement;
 import org.flowable.bpmn.model.BpmnModel;
 import org.flowable.bpmn.model.ExtensionElement;
 import org.flowable.bpmn.model.FlowElement;
 import org.flowable.bpmn.model.Process;
 import org.flowable.bpmn.model.ServiceTask;
 import org.flowable.validation.ValidationError;
 import org.flowable.validation.validator.ValidatorImpl;


 public class InitVariablesServiceTaskValidator
   extends ValidatorImpl
   implements PlatformServiceTaskValidator
 {
   private static final String DELEGATE_EXPRESSION = "${initVariablesService}";
   private static final String VARIABLE_MAPPING = "variableMapping";
   private static final String ATTRIBUTE_TARGET = "target";
   private static final String ATTRIBUTE_NAME = "name";
   private static final String ATTRIBUTE_VALUE = "value";
   private static final String ATTRIBUTE_VALUE_EXPRESSION = "valueExpression";
   private static final String ATTRIBUTE_VALUE_TYPE = "valueType";
   private static final String VALUE_TYPE_JSON_OBJECT = "jsonObject";
   private static final String VALUE_TYPE_JSON_ARRAY = "jsonArray";
   private static final Set<String> KNOWN_VALUE_TYPES = new HashSet<>(
       Arrays.asList(new String[] { "integer", "long", "double", "localDate", "string", "boolean", "variable", "jsonObject", "jsonArray" }));

   public void validate(BpmnModel bpmnModel, List<ValidationError> list) {
     throw new UnsupportedOperationException("This validator can only be used as a " + PlatformServiceTaskValidator.class);
   }
 
   
   public String getDelegateExpression() {
     return "${initVariablesService}";
   }
 
   
   public void validate(BpmnModel bpmnModel, Process process, ServiceTask serviceTask, List<ValidationError> errors) {
     List<ExtensionElement> variableMappings = (List<ExtensionElement>)serviceTask.getExtensionElements().getOrDefault("variableMapping", Collections.emptyList());
     if (variableMappings.isEmpty()) {
       addWarning(errors, "init-variables-no-variable-mappings", process, (BaseElement)serviceTask, "There are no defined variable mappings");
     } else {
       for (ExtensionElement variableMapping : variableMappings) {
         validateVariableMapping(bpmnModel, process, serviceTask, variableMapping, errors);
       }
     } 
   }
 
 
   
   protected void validateVariableMapping(BpmnModel bpmnModel, Process process, ServiceTask serviceTask, ExtensionElement variableMapping, List<ValidationError> errors) {
     String target = variableMapping.getAttributeValue(null, "target");
     String name = variableMapping.getAttributeValue(null, "name");
     if (StringUtils.isAllEmpty(new CharSequence[] { target, name })) {
       addError(errors, "init-variables-no-target-or-name", process, (FlowElement)serviceTask, (BaseElement)variableMapping, "At least one of 'target' or 'name' must be defined");
     }
 
     
     String valueType = variableMapping.getAttributeValue(null, "valueType");
     if (StringUtils.isNotEmpty(valueType) && !KNOWN_VALUE_TYPES.contains(valueType)) {
       addWarning(errors, "init-variables-unknown-value-type", process, (FlowElement)serviceTask, (BaseElement)variableMapping, "Unknown value type");
     }
     
     String value = variableMapping.getAttributeValue(null, "value");
     String valueExpression = variableMapping.getAttributeValue(null, "valueExpression");
     if (StringUtils.isNotEmpty(value) && StringUtils.isNotEmpty(valueExpression)) {
       addWarning(errors, "init-variables-variable-value-and-value-expression", process, (FlowElement)serviceTask, (BaseElement)variableMapping, "Both 'value' and 'valueExpression' are defined. 'value' will be ignored");
     }
     else if (!"jsonObject".equals(valueType) && StringUtils.isAllEmpty(new CharSequence[] { value, valueExpression })) {
       addWarning(errors, "init-variables-no-value-or-value-expression", process, (FlowElement)serviceTask, (BaseElement)variableMapping, "At least one of 'value' or 'valueExpression' must be defined");
     } 
   }
 }