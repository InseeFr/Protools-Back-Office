 package fr.insee.protools.backend.flowable.tasks.autoconfigure;
 
 import org.springframework.boot.context.properties.ConfigurationProperties;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 @ConfigurationProperties(prefix = "flowable.tasks.service-registry")
 public class ServiceRegistryTaskProperties
 {
   private boolean ignoreMissingDbServiceResponses = false;
   
   public boolean isIgnoreMissingDbServiceResponses() {
     return this.ignoreMissingDbServiceResponses;
   }
   
   public void setIgnoreMissingDbServiceResponses(boolean ignoreMissingDbServiceResponses) {
     this.ignoreMissingDbServiceResponses = ignoreMissingDbServiceResponses;
   }
 }


/* Location:              C:\INSEE\DEV\tmp\flowable-trial\flowable\tomcat\webapps\flowable-work.war!\WEB-INF\lib\flowable-spring-boot-starter-tasks-3.15.3.jar!\com\flowable\autoconfigure\tasks\ServiceRegistryTaskProperties.class
 * Java compiler version: 17 (61.0)
 * JD-Core Version:       1.1.3
 */