package fr.insee.protools.backend.restclient.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class APIProperties {

    @NotBlank
    @Pattern(regexp = "^https?://[^\\s/$.?#].[^\\s]*$",
            message = "Invalid URL format")
    private String url;
    @Valid
    private AuthProperties auth;
    private Boolean enabled = Boolean.FALSE;
    private Duration readTimeout = Duration.ofSeconds(20);  // Default 20 seconds
    private Duration exchangeTimeout = Duration.ofSeconds(20);  // Default 20 seconds

    @Data
    @NoArgsConstructor
    //Infos to retrieve an oath token
    public static class AuthProperties {
        @NotBlank
        @Pattern(regexp = "^https?://[^\\s/$.?#].[^\\s]*$",
                message = "Invalid URL format")
        private String url;
        @NotBlank
        private String realm;
        @NotBlank
        private String clientId;
        @NotBlank
        @JsonIgnore
        private String clientSecret;

        public AuthProperties(String url, String realm, String clientId, String clientSecret) {
            this.url = url;
            this.realm = realm;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
        }

        @Override
        public String toString() {
            return "AuthProperties{" +
                    "url='" + url + '\'' +
                    ", realm='" + realm + '\'' +
                    ", clientId='" + clientId + '\'' +
                    '}';
        }
    }

}