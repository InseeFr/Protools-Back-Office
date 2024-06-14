package fr.insee.protools.backend.flowable.tasks;


import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.flowable.tasks.autoconfigure.ServiceRegistryTaskProperties;
import fr.insee.protools.backend.flowable.tasks.initvariables.InitVariablesService;
import fr.insee.protools.backend.flowable.tasks.initvariables.ProcessInitVariablesService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@EnableConfigurationProperties({ServiceRegistryTaskProperties.class})
public class TasksAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean({ProcessInitVariablesService.class})
    public ProcessInitVariablesService processInitVariablesService() {
        return new ProcessInitVariablesService();
    }


    @Bean
    @ConditionalOnMissingBean({InitVariablesService.class})
    public InitVariablesService initVariablesService(ObjectMapper objectMapper) {
        return new InitVariablesService(objectMapper);
    }

}
