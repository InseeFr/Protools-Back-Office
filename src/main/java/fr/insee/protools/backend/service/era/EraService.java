package fr.insee.protools.backend.service.era;

import fr.insee.protools.backend.service.era.dto.CensusJsonDto;
import fr.insee.protools.backend.service.era.dto.GenderType;
import fr.insee.protools.backend.webclient.WebClientHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static fr.insee.protools.backend.webclient.configuration.ApiConfigProperties.KNOWN_API.KNOWN_API_ERA;

@Service
@Slf4j
public class EraService {
    @Autowired WebClientHelper webClientHelper;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<CensusJsonDto> getSUForPeriodAndSex(LocalDate startDate , LocalDate endDate, GenderType gender){
        log.info("getSUForPeriodAndSex - gender={} from {} to {}", gender, startDate, endDate);
        ParameterizedTypeReference<List<CensusJsonDto>> typeReference = new ParameterizedTypeReference<List<CensusJsonDto>>(){};

        List<CensusJsonDto> response = webClientHelper.getWebClient(KNOWN_API_ERA)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/census-extraction/census-respondents-by-period-and-gender")
                        .queryParam("startDate", startDate.format(dateFormatter))
                        .queryParam("endDate", endDate.format(dateFormatter))
                        .queryParam("gender", gender)
                        .build())
                .retrieve()
                .bodyToMono(typeReference)
                .block();
        log.trace("getSUForPeriodAndSex - response={} ", response);
        return response;
    }
}
