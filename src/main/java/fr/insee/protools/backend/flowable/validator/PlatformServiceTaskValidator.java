package fr.insee.protools.backend.flowable.validator;

import java.util.List;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.validation.ValidationError;

public interface PlatformServiceTaskValidator {
  String getDelegateExpression();
  
  void validate(BpmnModel paramBpmnModel, Process paramProcess, ServiceTask paramServiceTask, List<ValidationError> paramList);
}
