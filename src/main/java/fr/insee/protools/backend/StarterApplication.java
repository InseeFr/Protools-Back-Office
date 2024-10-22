package fr.insee.protools.backend;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class StarterApplication {

        public static void main(String[] args) {
                configureApplicationBuilder(new SpringApplicationBuilder()).build().run(args);        }

        public static SpringApplicationBuilder configureApplicationBuilder(SpringApplicationBuilder springApplicationBuilder){
                return springApplicationBuilder.sources(StarterApplication.class);
        }

        @EventListener(ApplicationReadyEvent.class)
        public void springdocStopIgnoringHttpServletRequest() {
                //In springdoc : stop ignoring the HttpServletRequest parameters as they are used by Flowable
                SpringDocUtils.getConfig().removeRequestWrapperToIgnore(HttpServletRequest.class);
                SpringDocUtils.getConfig().removeRequestWrapperToIgnore(ServletRequest.class);
        }

}
