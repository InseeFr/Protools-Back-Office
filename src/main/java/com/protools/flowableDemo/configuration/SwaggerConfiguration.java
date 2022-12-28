package com.protools.flowableDemo.configuration;

import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Autowired
    BuildProperties buildProperties;


    Contact contact = new Contact()
            .name("Protools")
            .url("https://github.com/InseeFr/Protools-Back-Office");


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title(buildProperties.getName())
                        .description("Back-Office Service for Protools orchestrator")
                        .version(buildProperties.getVersion())
                        .contact(contact));

    }


}
