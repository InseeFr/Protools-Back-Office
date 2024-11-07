package fr.insee.protools.backend.restclient.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
public class ApiConfigProperties {

    public APIProperties getAPIProperties(KNOWN_API api) {
        return switch (api) {
            case KNOWN_API_PLATINE_PILOTAGE -> platinePilotageApiProperties();
            case KNOWN_API_PLATINE_QUESTIONNAIRE -> platineQuestionnaireApiProperties();
            case KNOWN_API_ERA -> eraApiProperties();
            case KNOWN_API_SABIANE_PILOTAGE -> sabianePilotageApiProperties();
            case KNOWN_API_SABIANE_QUESTIONNAIRE -> sabianeQuestionnaireApiProperties();
            case KNOWN_API_REM -> remApiProperties();
            case KNOWN_API_MESHUGGAH -> meshuggahApiProperties();
            case KNOWN_API_SUGOI -> sugoiApiProperties();
            case KNOWN_API_TRAITERXXX -> traiterXXXApiProperties();
        };
    }

    @Bean("eraApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.era")
    @Validated
    public APIProperties eraApiProperties() {
        return new APIProperties();
    }

    @Bean("platinePilotageApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.platine-pilotage")
    public APIProperties platinePilotageApiProperties() {
        return new APIProperties();
    }

    @Bean("platineQuestionnaireApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.platine-questionnaire")
    public APIProperties platineQuestionnaireApiProperties() {
        return new APIProperties();
    }

    @Bean("sabianePilotageApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.sabiane-pilotage")
    public APIProperties sabianePilotageApiProperties() {
        return new APIProperties();
    }

    @Bean("sabianeQuestionnaireApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.sabiane-questionnaire")
    public APIProperties sabianeQuestionnaireApiProperties() {
        return new APIProperties();
    }

    @Bean("remApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.rem")
    public APIProperties remApiProperties() {
        return new APIProperties();
    }

    @Bean("meshuggahApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.meshuggah")
    public APIProperties meshuggahApiProperties() {
        return new APIProperties();
    }

    @Bean("sugoiApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.sugoi")
    public APIProperties sugoiApiProperties() {
        return new APIProperties();
    }

    @Bean("traiterXXXApiProperties")
    @ConfigurationProperties("fr.insee.protools.api.traiterxxx")
    public APIProperties traiterXXXApiProperties() {
        return new APIProperties();
    }

    public enum KNOWN_API {
        KNOWN_API_PLATINE_PILOTAGE,
        KNOWN_API_PLATINE_QUESTIONNAIRE,
        KNOWN_API_ERA,
        KNOWN_API_SABIANE_PILOTAGE,
        KNOWN_API_SABIANE_QUESTIONNAIRE,
        KNOWN_API_REM,
        KNOWN_API_MESHUGGAH,
        KNOWN_API_SUGOI,
        KNOWN_API_TRAITERXXX
    }

}
