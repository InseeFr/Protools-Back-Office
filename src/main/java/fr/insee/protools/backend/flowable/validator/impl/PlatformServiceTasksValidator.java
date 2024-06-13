/*    */
package fr.insee.protools.backend.flowable.validator.impl;
/*    */ 
/*    */

import fr.insee.protools.backend.flowable.validator.PlatformServiceTaskValidator;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.ProcessLevelValidator;

import java.util.*;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlatformServiceTasksValidator
/*    */   extends ProcessLevelValidator
/*    */ {
/* 29 */   protected final Map<String, List<PlatformServiceTaskValidator>> validators = new LinkedHashMap<>();
/*    */ 
/*    */   
/*    */   protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
/* 33 */     List<ServiceTask> serviceTasks = process.findFlowElementsOfType(ServiceTask.class);
/*    */     
/* 35 */     for (ServiceTask serviceTask : serviceTasks) {
/* 36 */       executeValidationForServiceTask(bpmnModel, process, serviceTask, errors);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   protected void executeValidationForServiceTask(BpmnModel bpmnModel, Process process, ServiceTask serviceTask, List<ValidationError> errors) {
/* 42 */     if ("delegateExpression".equals(serviceTask.getImplementationType()) && 
/* 43 */       StringUtils.isNotEmpty(serviceTask.getImplementation())) {
/*    */       
/* 45 */       List<PlatformServiceTaskValidator> serviceTaskValidators = this.validators.getOrDefault(serviceTask.getImplementation(), Collections.emptyList());
/*    */       
/* 47 */       for (PlatformServiceTaskValidator validator : serviceTaskValidators) {
/* 48 */         validator.validate(bpmnModel, process, serviceTask, errors);
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   public void addValidator(PlatformServiceTaskValidator validator) {
/* 54 */     ((List<PlatformServiceTaskValidator>)this.validators.computeIfAbsent(validator.getDelegateExpression(), key -> new ArrayList())).add(validator);
/*    */   }
/*    */   
/*    */   public Map<String, List<PlatformServiceTaskValidator>> getValidators() {
/* 58 */     return this.validators;
/*    */   }
/*    */ }


/* Location:              C:\INSEE\DEV\tmp\flowable-trial\flowable\tomcat\webapps\flowable-work.war!\WEB-INF\lib\flowable-platform-validation-3.15.3.jar!\com\flowable\validation\bpmn\impl\PlatformServiceTasksValidator.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */