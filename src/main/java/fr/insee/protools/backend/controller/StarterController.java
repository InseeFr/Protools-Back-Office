package fr.insee.protools.backend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.protools.backend.restclient.RestClientHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequestMapping("/starter")
@RestController
@RequiredArgsConstructor
public class StarterController {

    private final Optional<BuildProperties> buildProperties;

    private final RestClientHelper restClientHelper;

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthcheck() {
        return ResponseEntity.ok(
                """
                             OK
                        
                             Version %s
                             Utilisateur %s
                        """
                        .formatted(
                                buildProperties.map(BuildProperties::getVersion).orElse("n.a"),
                                SecurityContextHolder.getContext().getAuthentication().getName()
                        ));
    }

    @GetMapping("/healthcheckadmin")
    public ResponseEntity<String> healthcheckadmin() {
        return ResponseEntity.ok(
                """
                             OK
                        
                             Version %s
                             Administrateur %s
                        """
                        .formatted(
                                buildProperties.map(BuildProperties::getVersion).orElse("n.a"),
                                SecurityContextHolder.getContext().getAuthentication().getName()
                        )
        );
    }

    @GetMapping("/token_details_by_api")
    public ResponseEntity<String> tokensDetailsByAPI() {
        StringBuilder result = new StringBuilder("List of tokens roles : ");
        for (var x : restClientHelper.getTokenDetailsByAPI().entrySet()) {
            result.append("\n").append(x.getKey()).append(" : ").append(x.getValue());
        }
        return ResponseEntity.ok(result.toString());
    }

    @GetMapping("/api_configuration")
    public ResponseEntity<JsonNode> apiConfiguration() {
        return ResponseEntity.ok(restClientHelper.getAPIConfigDetails());
    }

    @GetMapping(value = "/changelog", produces = MediaType.TEXT_PLAIN_VALUE)
    public Resource changelog() {

        return new ClassPathResource("changelog.md");
    }


}
