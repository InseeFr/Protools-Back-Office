package fr.insee.protools.backend.flowable.tasks.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.flowable.tasks.InitVariablesService;
import fr.insee.protools.backend.flowable.tasks.ProcessInitVariablesService;
import fr.insee.protools.backend.flowable.validator.FlowablePlatformValidatorSetFactory;
import org.flowable.validation.validator.ValidatorSet;
import org.flowable.validation.validator.impl.ServiceTaskValidator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;


@AutoConfiguration
@EnableConfigurationProperties({ServiceRegistryTaskProperties.class})
public class TasksAutoConfiguration {
    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean({ProcessInitVariablesService.class})
    @Deprecated
    public ProcessInitVariablesService processInitVariablesService() {
        return new ProcessInitVariablesService();
    }

    @Bean
    @ConditionalOnMissingBean({InitVariablesService.class})
    public InitVariablesService initVariablesService(ObjectMapper objectMapper) {
        return new InitVariablesService(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(name = {"flowablePlatformServiceTaskValidator"})
    public ServiceTaskValidator flowablePlatformServiceTaskValidator() {
        return new FlowablePlatformServiceTaskValidator();
    }

    @Bean
    @ConditionalOnMissingBean(name = {"flowablePlatformValidatorSet"})
    public ValidatorSet flowablePlatformValidatorSet() {
        return (new FlowablePlatformValidatorSetFactory()).createFlowablePlatformValidatorSet();
    }
}

