package fr.insee.protools.backend.service.rem;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.restclient.pagination.PageResponse;

import java.util.List;
import java.util.Map;

public interface IRemService {

    PageResponse<JsonNode> getPartitionAllInterroPaginated(String partitionId, long page);

    List<JsonNode> getLitOfInterro(List<String> interroIdList);

    List<String> getInterrogationIdsWithoutAccountForPartition(String partitionId);

    void patchInterrogationsSetAccounts(Map<String, String> userByInterroId);

    void putContactsPlatine(List<JsonNode> contactPlatineList);

    void postRemiseEnCollecte(List<JsonNode> interroRemiseEnCollecteList);
}