package fr.insee.protools.backend.integration.delegate_and_services_stub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import fr.insee.protools.backend.service.rem.IRemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RemServiceStub implements IRemService {

    private final static ObjectMapper objectMapper=new ObjectMapper();

    public final static List<JsonNode> defaultInterroList = List.of(objectMapper.createObjectNode().put("xx","yyy"));
    public final static List<String> defaultinterrogationIdWithoutAccountList=List.of("55","77");
    public final static Integer defaultCurrentPage=0;
    public final static Integer defaultPageCount=1;
    public final static Boolean isLastPage=Boolean.TRUE;

    @Override
    public List<JsonNode> getLitOfInterro(List<String> interroIdList) {
        return List.of();
    }

    @Override
    public PageResponse<JsonNode> getPartitionAllInterroPaginated(String partitionId, long page) {
        return PageResponse.<JsonNode>builder().currentPage(defaultCurrentPage).pageCount(defaultPageCount).content(defaultInterroList).build();
    }

    @Override
    public List<String> getInterrogationIdsWithoutAccountForPartition(String partitionId) {
        return new ArrayList<>(defaultinterrogationIdWithoutAccountList);
    }

    @Override
    public void patchInterrogationsSetAccounts(Map<String, String> userByInterroId) {
        return ;
    }

    @Override
    public void putContactsPlatine(List<JsonNode> contactPlatineList) {
        return ;
    }

    @Override
    public void postRemiseEnCollecte(List<JsonNode> interroRemiseEnCollecteList) {
        return ;
    }
}
