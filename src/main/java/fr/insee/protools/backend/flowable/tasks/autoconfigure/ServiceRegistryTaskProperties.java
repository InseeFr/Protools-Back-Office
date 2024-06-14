package fr.insee.protools.backend.flowable.tasks.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "flowable.tasks.service-registry")
public class ServiceRegistryTaskProperties {
    private boolean ignoreMissingDbServiceResponses = false;
}