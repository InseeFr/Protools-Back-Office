package fr.insee.protools.backend.flowable.tasks.bpmn;


import fr.insee.protools.backend.flowable.tasks.BaseExtensionElement;
import fr.insee.protools.backend.flowable.tasks.ExtensionElementsContainer;
import org.flowable.bpmn.model.BaseElement;

import java.util.Collections;
import java.util.stream.Stream;


public class BpmnExtensionElementsContainer
        implements ExtensionElementsContainer {
    protected final BaseElement baseElement;

    public BpmnExtensionElementsContainer(BaseElement baseElement) {
        this.baseElement = baseElement;
    }


    public String getId() {
        return this.baseElement.getId();
    }


    public Stream<BaseExtensionElement> getExtensionElements(String elementName) {
        return (this.baseElement.getExtensionElements()
                .getOrDefault(elementName, Collections.emptyList()))
                .stream()
                .map(BpmnExtensionElement::new);
    }
}
