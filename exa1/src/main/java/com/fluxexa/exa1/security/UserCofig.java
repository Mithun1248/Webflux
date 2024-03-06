package com.fluxexa.exa1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class UserCofig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
        return http.authorizeExchange(
                auth -> auth
                        .pathMatchers(HttpMethod.POST,"/").permitAll()
                        .pathMatchers(HttpMethod.GET,"/").permitAll()
                        .anyExchange().authenticated()
        )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(
                        cors -> cors.configurationSource(
                                request -> {
                                    CorsConfiguration configuration = new CorsConfiguration();
                                    configuration.setAllowedOrigins(List.of(""));
                                    configuration.setAllowedMethods(List.of("*"));
                                    configuration.setAllowedHeaders(List.of("*"));
                                    configuration.setExposedHeaders(List.of("*"));
                                    return configuration;
                                }
                        )
                ).oauth2ResourceServer(
                        oauth -> oauth.jwt(
                                jwtConfigure -> jwtConfigure.jwtAuthenticationConverter(new KeyCloakRoleConverter())
                        )
                )
                .build();
    }
}
