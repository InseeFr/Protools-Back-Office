package com.protools.flowableDemo.services.utils;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class SampleServiceTask implements JavaDelegate {
    private Logger logger =LogManager.getLogger(SampleServiceTask.class);
    @Override
    public void execute(DelegateExecution delegateExecution) {
        logger.info("\t >> Sample service Task <<  ");

    }
}
