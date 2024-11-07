package fr.insee.protools.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

//Va de pair avec la property server.forward-headers-strategy=framework
public class ForwardedHeaderFilterBean {

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}