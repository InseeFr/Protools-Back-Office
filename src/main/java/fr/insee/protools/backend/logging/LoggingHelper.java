package fr.insee.protools.backend.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


public class LoggingHelper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private LoggingHelper() {
    }

    @ExcludeFromJacocoGeneratedReport
    public static void logJson(String msg, Object dto, Logger logger, Level level) {
        if (logger.isEnabledForLevel(level) && msg != null) {
            try {
                String json = objectMapper.writeValueAsString(dto);
                String logLine = msg + " - " + json;
                switch (level) {
                    case TRACE -> logger.trace(logLine);
                    case DEBUG -> logger.debug(logLine);
                    case INFO -> logger.info(logLine);
                    case WARN -> logger.warn(logLine);
                    case ERROR -> logger.error(logLine);
                    default -> logger.trace(logLine);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse json");
            }
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExcludeFromJacocoGeneratedReport {
    }
}
