package com.fluxexa.exa1.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public class KeyCloakRoleConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        log.info("KeycloakRoleConverter has been called!");
        return Mono.justOrEmpty(createAuthenticationToken(jwt));
    }

    private AbstractAuthenticationToken createAuthenticationToken(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");

        if (realmAccess != null) {
            Collection<GrantedAuthority> authorities = getRoles(realmAccess);
            return new JwtAuthenticationToken(jwt, authorities);
        }

        return null;
    }

    private Collection<GrantedAuthority> getRoles(Map<String, Object> realmAccess) {
        if (realmAccess.containsKey("roles")) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            return roles.stream()
                    .map(roleName -> "ROLE_" + roleName)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
