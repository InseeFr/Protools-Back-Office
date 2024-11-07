package fr.insee.protools.backend.restclient.configuration.annotation;

import fr.insee.protools.backend.restclient.configuration.exception.RequestTimeoutHttpClientErrorException;
import fr.insee.protools.backend.restclient.configuration.exception.TooEarlyHttpClientErrorException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Retryable(
        maxAttempts = 5,
        backoff = @Backoff(delay = 1500, multiplier = 1.2),
        retryFor = {
                RequestTimeoutHttpClientErrorException.class,
                TooEarlyHttpClientErrorException.class,
                HttpServerErrorException.BadGateway.class,
                HttpServerErrorException.GatewayTimeout.class,
                HttpServerErrorException.ServiceUnavailable.class,
                HttpClientErrorException.TooManyRequests.class,
                ResourceAccessException.class
        }
)
public @interface RestClientRetryable {
}