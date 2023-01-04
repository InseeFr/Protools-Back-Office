package com.protools.flowableDemo.helpers.client.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfigProperties {

        public enum KNOWN_API {
                KNOWN_API_COLEMAN_PILOTAGE,
                KNOWN_API_COLEMAN_QUESTIONNAIRE,
                KNOWN_API_ERA
        }

        public APIProperties getAPIProperties(KNOWN_API api){
                switch (api){
                        case KNOWN_API_COLEMAN_PILOTAGE: return colemanPilotageApiProperties();
                        case KNOWN_API_COLEMAN_QUESTIONNAIRE: return colemanQuestionnaireApiProperties();
                        case KNOWN_API_ERA: return eraApiProperties();
                }
                return new APIProperties();
        }

        @Bean("eraApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.era")
        public APIProperties eraApiProperties() {
                return new APIProperties();
        }

        @Bean("colemanPilotageApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.coleman-pilotage")
        public APIProperties colemanPilotageApiProperties() {
                return new APIProperties();
        }

        @Bean("colemanQuestionnaireApiProperties")
        @ConfigurationProperties("fr.insee.protools.api.coleman-questionnaire")
        public APIProperties colemanQuestionnaireApiProperties() {
                return new APIProperties();
        }

}
