package fr.insee.protools.backend.dto.internal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.insee.protools.backend.dto.platine.pilotage.PlatinePilotageCommunicationEventType;

import java.io.Serializable;

@JsonSerialize
public record CommunicationRequestDetails(String communicationRequestId,
                                          PlatinePilotageCommunicationEventType event) implements Serializable {
}
