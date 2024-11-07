package fr.insee.protools.backend.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * Cette classe est utilisée pour enregistrer les requêtes API entrantes avec :
 * le nom d'utilisateur et l'adresse IP de l'appelant.
 * Elle crée un Bean custom CommonsRequestLoggingFilter.
 * Les seuls changements sont qu'elle modifie le niveau de log :
 * Par défaut, CommonsRequestLoggingFilter log uniquement lorsque le niveau de journalisation est défini sur debug.
 * Ici, nous écrivons un log AVANT la requête si le niveau INFO est actif.
 * <p>
 * <p>Le nom d'utilisateur loggé est celui défini dans le Principal de Spring security.
 * <p>Dans SecurityConfig de Protools,
 * nous définissons qu'il doit être initialisé avec le preferred_username des claims du jeton JWT.
 * <p>Dans le contexte INSEE, il s'agit de l'IDEP.
 * <p>
 * <p>Format des messages
 * <ul>
 * <li>Pour les utilisateurs non enregistrés:
 * <p>     - [GET /path/endpoint, client=192.168.0.200]
 *
 * <li>Pour les utilisateurs enregistrés:
 * <p>     - [GET /path/endpoint, client=127.0.0.1, user=<IDEP>]
 *
 * </ul>
 */
@Configuration
public class RequestLoggingFilterConfig {

    @Bean
    public CommonsRequestLoggingFilter loggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter() {
            @Override
            protected void beforeRequest(HttpServletRequest request, String message) {
                this.logger.info(message);
            }

            @Override
            protected boolean shouldLog(HttpServletRequest request) {
                return this.logger.isInfoEnabled();
            }
        };

        filter.setIncludeClientInfo(false);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);// Or true if you want to include headers
        return filter;
    }
}
