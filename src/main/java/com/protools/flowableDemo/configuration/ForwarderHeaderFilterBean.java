package com.protools.flowableDemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

public class ForwarderHeaderFilterBean {
    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
