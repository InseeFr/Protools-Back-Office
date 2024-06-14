package fr.insee.protools.backend.flowable.tasks.bpmn;

import java.util.Optional;
import java.util.stream.Stream;

public interface BaseExtensionElement {
    String getName();

    String getText();

    default Optional<String> getAttributeValue(String attributeName) {
        return getAttributes(attributeName).findFirst().map(BaseExtensionAttribute::getValue);
    }

    Stream<BaseExtensionAttribute> getAttributes(String paramString);

    Stream<BaseExtensionElement> getChildElements(String paramString);
}