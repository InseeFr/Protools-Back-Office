package fr.insee.protools.backend.service.sabiane.pilotage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReferentDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String role;
}