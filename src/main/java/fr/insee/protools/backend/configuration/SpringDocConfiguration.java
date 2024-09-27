package fr.insee.protools.backend.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class SpringDocConfiguration {

  private final InseeSpringdocProperties springdocProperties;
  private final Optional<BuildProperties> buildProperties;


  public static final String OAUTHSCHEME = "oAuth";
  public static final String BEARERSCHEME = "bearerAuth";

  //public final String SCHEMEBASIC = "basic";

  @Bean
  public OpenAPI customOpenAPIBasicAndOIDC() {
    final OpenAPI openapi = createOpenAPI();
    openapi.components(
        new Components()
            /*.addSecuritySchemes(
                SCHEMEBASIC,
                new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme(SCHEMEBASIC)
                    .in(SecurityScheme.In.HEADER)
                    .description("Authentification Basic"))*/
            .addSecuritySchemes(BEARERSCHEME,
                new SecurityScheme()
                    .name(BEARERSCHEME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
            .addSecuritySchemes(
                OAUTHSCHEME,
                new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .in(SecurityScheme.In.HEADER)
                    .description(springdocProperties.getIssuerDescription())
                    .flows(
                        new OAuthFlows()
                            //.implicit(
                            .authorizationCode(
                                new OAuthFlow()
                                    .authorizationUrl(springdocProperties.getIssuerUrlAuthorization())
                                    .tokenUrl(springdocProperties.getIssuerUrlToken())
                                    .refreshUrl(springdocProperties.getIssuerUrlRefresh())))));
    return openapi;
  }

  private OpenAPI createOpenAPI() {
    log.info("surcharge de la configuration swagger");
    Contact contact = new Contact();
    contact.name(springdocProperties.getContactName());

    if (springdocProperties.getContactUrl() != null) {
      contact.setUrl(springdocProperties.getContactUrl());
    }

    if (springdocProperties.getContactEmail() != null) {
      contact = contact.email(springdocProperties.getContactEmail());
    }
    return
        new OpenAPI()
            .info(
                new Info()
                    .title(buildProperties.map(BuildProperties::getName).orElse("n.a"))
                    .description(
                            String.format("`Application version : %s`\n\n%s",
                                    buildProperties.map(BuildProperties::getVersion).orElse("n.a"),
                                    springdocProperties.getDescription()
                            )
                    )
                    .version(buildProperties.map(BuildProperties::getVersion).orElse("n.a"))
                    /*.license(
                        new License()
                            .name("Apache 2.0")
                            .url("http://www.apache.org/licenses/LICENSE-2.0.html"))*/
                    .contact(contact));
  }

  @Bean
  public OperationCustomizer addAuth() {
    return (operation, handlerMethod) -> operation
       // .addSecurityItem(new SecurityRequirement().addList(SCHEMEBASIC))
        .addSecurityItem(new SecurityRequirement().addList(OAUTHSCHEME))
        .addSecurityItem(new SecurityRequirement().addList(BEARERSCHEME));

  }
}
