package com.protools.flowableDemo;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flowable.engine.RepositoryService;
import org.flowable.eventregistry.api.EventRepositoryService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

@SpringBootApplication
public class FlowablePocApplication {
	private Logger logger = LogManager.getLogger(FlowablePocApplication.class);
	private final RepositoryService repositoryService;

	public FlowablePocApplication(RepositoryService repositoryService, EventRepositoryService eventRepositoryService) {
		this.repositoryService = repositoryService;
	}

	public static void main(String[] args) {
		SpringApplication.run(FlowablePocApplication.class, args);
	}

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		final Environment env = event.getApplicationContext().getEnvironment();
		final String PREFIX = "fr.insee";
		final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
		logger.info("================================ Properties =================================");
		StreamSupport.stream(sources.spliterator(), false).filter(EnumerablePropertySource.class::isInstance)
				.map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames()).flatMap(Arrays::stream).distinct()
				.filter(prop -> !(prop.contains("credentials") || prop.contains("password") || prop.contains("pw")
						|| prop.contains("secret")))
				.filter(prop -> prop.startsWith(PREFIX) || prop.startsWith("logging") || prop.startsWith("keycloak")
						|| prop.startsWith("spring") || prop.startsWith("application"))
				.sorted().forEach(prop -> logger.info("{}: {}", prop, env.getProperty(prop)));
		logger.info("============================================================================");
		logger.info("Application started.");

		logger.info("Processes deployed: {}", repositoryService.createDeploymentQuery().count());

	}

}
