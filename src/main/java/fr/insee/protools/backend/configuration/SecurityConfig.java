package fr.insee.protools.backend.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String STARTER_SECURITY_ENABLED = "fr.insee.sndil.starter.security.enabled";
    //Par défaut, spring sécurity prefixe les rôles avec cette chaine
    private static final String ROLE_PREFIX = "ROLE_";
    private final InseeSecurityTokenProperties inseeSecurityTokenProperties;
    // Démonstration avec un rôle protégeant l'accès à un des endpoints
    @Value("${fr.insee.sndil.starter.role.administrateur}")
    private String administrateurRole;
    //Liste d'URL sur lesquels on n'applique pas de sécurité (swagger; actuator...)
    @Value("#{'${fr.insee.sndil.starter.security.whitelist-matchers}'.split(',')}")
    private String[] whiteList;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        //configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(List.of(org.springframework.web.cors.CorsConfiguration.ALL));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    //Filter with activated security
    @Bean
    @ConditionalOnProperty(name = STARTER_SECURITY_ENABLED, havingValue = "true")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) //disable sessions (stateless)
                //only allows https
                .requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.cors(Customizer.withDefaults());
        for (var pattern : whiteList) {
            http.authorizeHttpRequests(authorize ->
                    authorize
                            .requestMatchers(AntPathRequestMatcher.antMatcher(pattern)).permitAll()
            );
        }

        http.authorizeHttpRequests(authorize ->
                        authorize
                                //  .requestMatchers("/**").permitAll()
                                //  .requestMatchers(mvcMatcherBuilder.pattern("/api/users/**")).permitAll() //MVC Controller
                                //  .requestMatchers(AntPathRequestMatcher.antMatcher(h2ConsolePath + "/**")).permitAll()
                                //  .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET,"/cars")).permitAll() //REST
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/starter/healthcheck")).permitAll() //REST
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/starter/changelog")).permitAll() //REST
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/starter/healthcheckadmin")).hasRole(administrateurRole)
                                //We allow admin to access everything
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/**")).hasRole(administrateurRole)
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    //Filter with disabled security
    @Bean
    @ConditionalOnProperty(name = STARTER_SECURITY_ENABLED, havingValue = "false", matchIfMissing = true)
    public SecurityFilterChain filterChain_noSecurity(HttpSecurity http) throws Exception {
        //Allow frames to be able tu use the H2 web console
        http.headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()
                )
        );
        http.cors(Customizer.withDefaults());

        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setPrincipalClaimName(inseeSecurityTokenProperties.getOidcClaimUsername());
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return new Converter<Jwt, Collection<GrantedAuthority>>() {
            @Override
            @SuppressWarnings({"unchecked"})
            public Collection<GrantedAuthority> convert(Jwt source) {

                String[] claimPath = inseeSecurityTokenProperties.getOidcClaimRole().split("\\.");
                Map<String, Object> claims = source.getClaims();
                try {

                    for (int i = 0; i < claimPath.length - 1; i++) {
                        claims = (Map<String, Object>) claims.get(claimPath[i]);
                    }

                    List<String> roles = (List<String>) claims.getOrDefault(claimPath[claimPath.length - 1], new ArrayList<>());
                    //if we need to add customs roles to every connected user we could define this variable (static or from properties)
                    //roles.addAll(defaultRolesForUsers);
                    return roles.stream().map(s -> new GrantedAuthority() {
                        @Override
                        public String getAuthority() {
                            return ROLE_PREFIX + s;
                        }

                        @Override
                        public String toString() {
                            return getAuthority();
                        }
                    }).collect(Collectors.toUnmodifiableList());
                } catch (ClassCastException e) {
                    // role path not correctly found, assume that no role for this user
                    return new ArrayList<>();
                }
            }
        };
    }
}


