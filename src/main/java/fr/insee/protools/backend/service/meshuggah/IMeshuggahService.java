package fr.insee.protools.backend.service.meshuggah;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.MeshuggahCommunicationRequestReponse;

import java.util.List;

public interface IMeshuggahService {

    void postContext(String campaignId, JsonNode contextRootNode);

    List<MeshuggahCommunicationRequestReponse> postCommunicationRequest(String campaignId, String communicationId, List<JsonNode> list);
}
