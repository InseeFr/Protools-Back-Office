package fr.insee.protools.backend.flowable.tasks;

import java.util.Optional;
import java.util.stream.Stream;

public interface ExtensionElementsContainer {
    String getId();

    default Optional<BaseExtensionElement> getExtensionElement(String elementName) {
        return getExtensionElements(elementName).findFirst();
    }

    Stream<BaseExtensionElement> getExtensionElements(String paramString);
}
