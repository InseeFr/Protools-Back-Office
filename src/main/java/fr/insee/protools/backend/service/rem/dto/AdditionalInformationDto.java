package fr.insee.protools.backend.service.rem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
public class AdditionalInformationDto {

    private String key;
    private String value;
}