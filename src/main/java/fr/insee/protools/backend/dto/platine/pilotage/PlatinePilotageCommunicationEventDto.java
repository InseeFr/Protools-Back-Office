package fr.insee.protools.backend.dto.platine.pilotage;

public record PlatinePilotageCommunicationEventDto(
        String interrogationId,
        String communicationId,
        String communicationRequestId,
        PlatinePilotageCommunicationEventType state
) {
}