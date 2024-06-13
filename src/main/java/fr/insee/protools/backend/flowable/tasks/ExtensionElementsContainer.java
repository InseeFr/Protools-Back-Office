 package fr.insee.protools.backend.flowable.tasks;
 
 import java.util.Optional;
 import java.util.stream.Stream;
 
 
 
 
 
 
 
 
 
 public interface ExtensionElementsContainer
 {
   String getId();
   
   default Optional<BaseExtensionElement> getExtensionElement(String elementName) {
     return getExtensionElements(elementName).findFirst();
   }
   
   Stream<BaseExtensionElement> getExtensionElements(String paramString);
 }


/* Location:              C:\INSEE\DEV\tmp\flowable-trial\flowable\tomcat\webapps\flowable-work.war!\WEB-INF\lib\flowable-platform-tasks-3.15.3.jar!\com\flowable\platform\tasks\ExtensionElementsContainer.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */