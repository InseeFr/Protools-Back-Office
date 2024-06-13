package fr.insee.protools.backend.flowable.tasks.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.validation.ValidationError;
import org.flowable.validation.validator.impl.ServiceTaskValidator;

import java.util.List;


public class FlowablePlatformServiceTaskValidator
        extends ServiceTaskValidator {
    protected void validateUnknownServiceTaskType(Process process, ServiceTask serviceTask, List<ValidationError> errors) {
        if (StringUtils.isNotEmpty(serviceTask.getType())) {

            switch (serviceTask.getType()) {
                case "data-object":
                    return;

                case "generate-document":
                    return;

                case "merge-document":
                    return;

                case "convert-document-to-pdf":
                    return;

                case "create-document":
                    return;

                case "service-registry":
                    return;

                case "audit":
                    return;
            }
            addError(errors, "flowable-servicetask-invalid-type", process, serviceTask, "Invalid or unsupported service task type");
        }
    }
}