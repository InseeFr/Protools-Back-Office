package fr.insee.protools.backend.flowable.tasks;

import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.springframework.beans.factory.annotation.Autowired;


public class ProcessInitVariablesService
        implements JavaDelegate {
    protected Expression initVariableMap;
    protected Expression overwrite;
    @Autowired
    protected InitVariablesService initVariablesService;

    public void execute(DelegateExecution delegateExecution) {
        this.initVariablesService.initVariables(CommandContextUtil.getProcessEngineConfiguration().getExpressionManager(), delegateExecution, this.initVariableMap
                .getExpressionText(), this.overwrite);
    }
}
