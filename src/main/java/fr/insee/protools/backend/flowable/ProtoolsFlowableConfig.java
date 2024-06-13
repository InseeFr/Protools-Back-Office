package fr.insee.protools.backend.flowable;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.impl.el.DefaultExpressionManager;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProtoolsFlowableConfig {

    private final ProcessEngine processEngine;

    @PostConstruct
    public void configureElResolver() {
        ProcessEngineConfigurationImpl configuration = (ProcessEngineConfigurationImpl) processEngine.getProcessEngineConfiguration();
       if (configuration.getExpressionManager() instanceof  DefaultExpressionManager) {
           ((DefaultExpressionManager) configuration.getExpressionManager()).addPreDefaultResolver(new HierarchyVariableELResolver());
       }
    }

}
