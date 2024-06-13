package fr.insee.protools.backend.flowable;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.impl.el.DefaultExpressionManager;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProtoolsFlowableProcessEnfineConfiguration {

    private final ProcessEngine processEngine;

    @PostConstruct
    public void ProtoolsProcessEngineConfiguration() {
        ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
        if (configuration instanceof ProcessEngineConfigurationImpl && ((ProcessEngineConfigurationImpl) configuration).getExpressionManager() instanceof DefaultExpressionManager) {
            ProcessEngineConfigurationImpl configurationImpl = (ProcessEngineConfigurationImpl) configuration;
            ExpressionManager expressionManager = configurationImpl.getExpressionManager();
            if (expressionManager instanceof DefaultExpressionManager) {
                ((DefaultExpressionManager) expressionManager).addPreDefaultResolver(new HierarchyVariableELResolver());
            }
        }
    }
}
