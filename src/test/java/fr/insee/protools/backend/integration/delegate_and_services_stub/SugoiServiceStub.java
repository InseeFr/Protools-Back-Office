package fr.insee.protools.backend.integration.delegate_and_services_stub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.dto.sugoi.Habilitation;
import fr.insee.protools.backend.dto.sugoi.User;
import fr.insee.protools.backend.restclient.pagination.PageResponse;
import fr.insee.protools.backend.service.rem.IRemService;
import fr.insee.protools.backend.service.sugoi.ISugoiService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SugoiServiceStub implements ISugoiService {

    @Override
    public User postCreateUser(User userBody) {
        Habilitation PLATINE_HABILITATION = new Habilitation("platine", "repondant", null);
        String userName = "USER_" + RandomStringUtils.randomAlphanumeric(3);
        return User.builder().username(userName).habilitations(List.of(PLATINE_HABILITATION)).build();
    }

    @Override
    public void postInitPassword(String userId, String password) {
        return;
    }
}
