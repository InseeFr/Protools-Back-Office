package fr.insee.protools.backend.configuration;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;

@Configuration
@ConfigurationProperties(
        prefix = "fr.insee.sndil.starter.security.token"
)
@ConditionalOnWebApplication
@Data
public class InseeSecurityTokenProperties {

    //Chemin pour récupérer la liste des rôles dans le jwt (token)
    private String oidcClaimRole;
    //Chemin pour récupérer le username dans le jwt (token)
    private String oidcClaimUsername = StandardClaimNames.PREFERRED_USERNAME;
}
