package com.protools.flowableDemo.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.protools.flowableDemo.utilities.TimeUtility;
import lombok.Data;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

@Data
public class Dates {
    @JacksonXmlProperty(localName = "DateDebutCollecte")
    private ZonedDateTime collectionStartDate;

    @JacksonXmlProperty(localName = "DateFinCollecte")
    private ZonedDateTime collectionEndDate;

    public void setCollectionStartDate(String collectionStartDate) throws DateTimeParseException {
        this.collectionStartDate = TimeUtility.parseToZonedDateTime(collectionStartDate);
    }

    public void setCollectionEndDate(String collectionEndDate) throws DateTimeParseException {
        this.collectionEndDate = TimeUtility.parseToZonedDateTime(collectionEndDate);
    }
}
