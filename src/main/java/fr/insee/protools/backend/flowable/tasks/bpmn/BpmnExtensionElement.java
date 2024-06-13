package fr.insee.protools.backend.flowable.tasks.bpmn;


import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import fr.insee.protools.backend.flowable.tasks.BaseExtensionAttribute;
import fr.insee.protools.backend.flowable.tasks.BaseExtensionElement;
import org.flowable.bpmn.model.ExtensionElement;


public class BpmnExtensionElement
        implements BaseExtensionElement {
    protected final ExtensionElement extensionElement;

    public BpmnExtensionElement(ExtensionElement extensionElement) {
        this.extensionElement = extensionElement;
    }


    public String getName() {
        return this.extensionElement.getName();
    }


    public String getText() {
        return this.extensionElement.getElementText();
    }


    public Stream<BaseExtensionAttribute> getAttributes(String attributeName) {
        return (this.extensionElement.getAttributes()
                .getOrDefault(attributeName, Collections.emptyList()))
                .stream()
                .map(BpmnExtensionAttribute::new);
    }


    public Stream<BaseExtensionElement> getChildElements(String elementName) {
        return (this.extensionElement.getChildElements()
                .getOrDefault(elementName, Collections.emptyList()))
                .stream()
                .map(BpmnExtensionElement::new);
    }
}
