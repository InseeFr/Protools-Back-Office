package fr.insee.protools.backend.flowable.tasks.bpmn;

import org.flowable.bpmn.model.ExtensionAttribute;


public class BpmnExtensionAttribute
        implements BaseExtensionAttribute {
    protected final ExtensionAttribute extensionAttribute;

    public BpmnExtensionAttribute(ExtensionAttribute extensionAttribute) {
        this.extensionAttribute = extensionAttribute;
    }


    public String getName() {
        return this.extensionAttribute.getName();
    }


    public String getValue() {
        return this.extensionAttribute.getValue();
    }
}
