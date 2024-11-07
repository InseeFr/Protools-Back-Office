package fr.insee.protools.backend.configuration;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.springdoc.core.utils.Constants.SPRINGDOC_SWAGGER_UI_ENABLED;

@Configuration
@ConfigurationProperties(
        prefix = "fr.insee.sndil.starter.springdoc"
)
@ConditionalOnProperty(name = SPRINGDOC_SWAGGER_UI_ENABLED, matchIfMissing = true)
@Data
public class InseeSpringdocProperties {

    private String issuerUrlAuthorization;
    private String issuerUrlRefresh;
    private String issuerUrlToken;
    private String issuerDescription;
    private String contactName;
    private String contactEmail;
    private String contactUrl;
    private String description;
}
