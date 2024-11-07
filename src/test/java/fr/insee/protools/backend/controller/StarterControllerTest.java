package fr.insee.protools.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.protools.backend.restclient.RestClientHelper;
import fr.insee.protools.backend.restclient.configuration.APIProperties;
import fr.insee.protools.backend.restclient.configuration.ApiConfigProperties;
import fr.insee.protools.backend.restclient.keycloak.KeycloakService;
import fr.insee.protools.backend.service.utils.CustomJWTHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = StarterController.class)
class StarterControllerTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @MockBean
    ApiConfigProperties apiConfigProperties;

    @SpyBean
    KeycloakService kcService;

    @MockBean
    JwtDecoder jwtDecoder;

    @SpyBean
    RestClientHelper restClientHelper;

    @Autowired private MockMvc mockMvc;

    @Test
    void gethealthchek_should_returnDefaultMessage() throws Exception {
        this.mockMvc.perform(get("/starter/healthcheck").with(jwt()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("OK")));


                mockMvc.perform(get("/starter/healthcheck").with(jwt()
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL"))).jwt(jwt -> {
                            jwt.subject("Ch4mpy");
                            jwt.claims(claims -> claims.put(StandardClaimNames.PREFERRED_USERNAME, "Tonton Pirate"));
                        })))
                        .andDo(print())
                        .andExpect(content().string(containsString("OK")))
                        .andExpect(content().string(containsString("Ch4mpy")));

    }


//    @Test
//    void getTokenDetails_should_returnCorrectly() throws Exception {
//
//        //Prepare : Only Platine pilotage is disable ; All other are confired and returns the same token
//        APIProperties.AuthProperties kcAuth = new APIProperties.AuthProperties("https://test.test","realm", "client","xxx");
//        APIProperties apiPropertiesAll = new APIProperties("url-all",kcAuth ,true);
//        APIProperties apiPropertiesPlatinePil = new APIProperties("url-platine",kcAuth ,false);
//
//        doReturn(apiPropertiesAll).when(apiConfigProperties).getAPIProperties(any());
//        doReturn(apiPropertiesPlatinePil).when(apiConfigProperties).getAPIProperties(eq(ApiConfigProperties.KNOWN_API.KNOWN_API_PLATINE_PILOTAGE));
//        //Token returned by getToken
//        doReturn(CustomJWTHelper.getEncodedToken(List.of("ROLE_administrateur_Platine","ROLE_Administrateurs_BEATLES"))).when(kcService).getToken(eq(kcAuth));
//
//        this.mockMvc.perform(
//                get("/starter/token_details_by_api").with(jwt()))
//                .andDo(print()).andExpect(status().isOk())
//                .andExpect(content().string(containsString("List of tokens roles ")))
//                .andExpect(content().string(containsString("KNOWN_API_PLATINE_PILOTAGE : API KNOWN_API_PLATINE_PILOTAGE is disabled in properties")))
//                .andExpect(content().string(containsString("KNOWN_API_SABIANE_QUESTIONNAIRE : [\"ROLE_administrateur_Platine\",\"ROLE_Administrateurs_BEATLES\"]")))
//                .andExpect(content().string(containsString("KNOWN_API_SABIANE_PILOTAGE : [\"ROLE_administrateur_Platine\",\"ROLE_Administrateurs_BEATLES\"]")));
//    }
}
