package fr.insee.protools.backend.service.rem;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.dto.rem_tmp.InterrogationAccountDto;
import fr.insee.protools.backend.dto.rem_tmp.InterrogationIdentifiersDto;
import fr.insee.protools.backend.restclient.RestClientHelper;
import fr.insee.protools.backend.restclient.configuration.ApiConfigProperties;
import fr.insee.protools.backend.restclient.exception.runtime.HttpClient4xxBPMNError;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static fr.insee.protools.backend.restclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_REM;

@Service
@Slf4j
@RequiredArgsConstructor
public class RemServiceImpl implements IRemService {

    private static final ApiConfigProperties.KNOWN_API API = KNOWN_API_REM;
    private final RestClientHelper restClientHelper;
    @Value("${fr.insee.protools.api.rem.interrogation.page.size:5000}")
    private int pageSizeGetInterro;

    @Override
    public List<JsonNode> getLitOfInterro(List<String> interroIdList) {
        log.debug("getLitOfInterro: interroIdList={} -- begin", interroIdList);
        ParameterizedTypeReference<List<JsonNode>> typeReference = new ParameterizedTypeReference<>() {
        };
        List<JsonNode> response = restClientHelper.getRestClient(API)
                .post()
                .uri("/interrogations/by_ids")
                .body(interroIdList)
                .retrieve()
                .body(typeReference);
        log.debug("getLitOfInterro: - response.size={} end", response.size());
        return response;
    }

    public PageResponse<JsonNode> getPartitionAllInterroPaginated(String partitionId, long page) {
        log.debug("getPartitionAllInterroPaginated: partitionId={} - page={} - pageSizeGetInterro={} - begin", partitionId, page, pageSizeGetInterro);
        ParameterizedTypeReference<PageResponse<JsonNode>> typeReference = new ParameterizedTypeReference<>() {
        };
        try {
            PageResponse<JsonNode> response = restClientHelper.getRestClient(API)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/interrogations")
                            .queryParam("page", page)
                            .queryParam("size", pageSizeGetInterro)
                            .queryParam("partition_id", partitionId)
                            .build())
                    .retrieve()
                    .body(typeReference);
            if (response == null) {
                response = new PageResponse<>();
            }
            log.trace("getPartitionAllInterroPaginated: partitionId={} - page={} - pageSizeGetInterro={} - response={} ", partitionId, page, pageSizeGetInterro, response.getContent().size());
            return response;
        } catch (HttpClient4xxBPMNError e) {
            if (e.getHttpStatusCodeError().equals(HttpStatus.NOT_FOUND)) {
                String msg =
                        "Error 404/NOT_FOUND during get sample on REM with partitionId=" + partitionId
                                + " - msg=" + e.getMessage();
                log.error(msg);
                throw new HttpClient4xxBPMNError(msg, e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }

    public List<String> getInterrogationIdsWithoutAccountForPartition(String partitionId) {
        log.debug("getInterrogationIdsWithoutAccountForPartition: - partitionId={} - begin", partitionId);
        ParameterizedTypeReference<List<InterrogationIdentifiersDto>> typeReference = new ParameterizedTypeReference<List<InterrogationIdentifiersDto>>() {
        };

        try {
            List<InterrogationIdentifiersDto> response = restClientHelper.getRestClient(API)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/interrogations/ids")
                            .queryParam("partition_id", partitionId)
                            .queryParam("hasAccount", false)
                            .build())
                    .retrieve()
                    .body(typeReference);

            List<String> interrogationIdsWithoutAccount =
                    response.stream()
                            .map(InterrogationIdentifiersDto::getInterrogationId)
                            .map(UUID::toString).toList();
            log.debug("getInterrogationIdsWithoutAccountForPartition: partitionId={} - interrogationIdsWithoutAccount.size={} - end", partitionId, interrogationIdsWithoutAccount.size());
            return interrogationIdsWithoutAccount;
        } catch (HttpClient4xxBPMNError e) {
            if (e.getHttpStatusCodeError().equals(HttpStatus.NOT_FOUND)) {
                String msg =
                        "Error 404/NOT_FOUND during get lot interrogations ids on REM with partitionId=" + partitionId
                                + " - msg=" + e.getMessage();
                log.error(msg);
                throw new HttpClient4xxBPMNError(msg, e.getHttpStatusCodeError());
            }
            //Currently no remediation so just rethrow
            throw e;
        }
    }

    public void patchInterrogationsSetAccounts(Map<String, String> userByInterroId) {
        log.debug("patchInterrogationsSetAccounts - userByInterroId.size={}", userByInterroId.size());

        List<InterrogationAccountDto> remDto = userByInterroId.entrySet()
                .stream()
                .map(entry -> InterrogationAccountDto.builder()
                        .interrogationId(UUID.fromString(entry.getKey()))
                        .account(entry.getValue())
                        .build())
                .toList();

        var response = restClientHelper.getRestClient(API)
                .patch()
                .uri("/interrogations/account")
                .body(remDto)
                .retrieve()
                .body(String.class);
        log.trace("patchInterrogationsSetAccounts - response={} ", response);
    }

    public void putContactsPlatine(List<JsonNode> contactPlatineList) {
        if (contactPlatineList == null || contactPlatineList.isEmpty()) {
            log.debug("putContactsPlatine ==> Nothing to do");
            return;
        }

        log.debug("putContactsPlatine - contactPlatineList.size={}", contactPlatineList);
        var response = restClientHelper.getRestClient(API)
                .put()
                .uri("/contacts-platine")
                .body(contactPlatineList)
                .retrieve()
                .body(String.class);
        log.trace("putContactsPlatine - response={} ", response);
    }

    public void postRemiseEnCollecte(List<JsonNode> interroRemiseEnCollecteList) {
        if (interroRemiseEnCollecteList == null || interroRemiseEnCollecteList.isEmpty()) {
            log.debug("postRemiseEnCollecte ==> Nothing to do");
            return;
        }

        log.debug("postRemiseEnCollecte - interroRemiseEnCollecteList.size={}", interroRemiseEnCollecteList);
        var response = restClientHelper.getRestClient(API)
                .post()
                .uri("/remise-en-collecte")
                .body(interroRemiseEnCollecteList)
                .retrieve()
                .body(String.class);
        log.trace("postRemiseEnCollecte - response={} ", response);
    }
}
