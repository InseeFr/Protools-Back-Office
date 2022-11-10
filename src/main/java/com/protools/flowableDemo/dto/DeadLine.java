package com.protools.flowableDemo.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.protools.flowableDemo.enums.DeadLineType;
import com.protools.flowableDemo.utilities.TimeUtility;
import lombok.Data;

import java.time.Duration;
import java.time.format.DateTimeParseException;

@Data
public class DeadLine {
    private DeadLineType type;

    @JacksonXmlText
    private String value;

    public boolean isValid() {
        return (type == DeadLineType.date && isValidDate()) || (type == DeadLineType.duree && isValidDuration());
    }

    private boolean isValidDate() {
        try {
            TimeUtility.parseToZonedDateTime(value);
            return true;
        }
        catch (DateTimeParseException ex) {
            return false;
        }
    }

    private boolean isValidDuration() {
        try {
            Duration.parse(value);
            return true;
        }
        catch (DateTimeParseException ex) {
            return false;
        }
    }
}
