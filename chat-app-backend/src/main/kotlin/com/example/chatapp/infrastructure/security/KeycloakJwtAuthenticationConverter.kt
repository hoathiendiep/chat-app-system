package com.example.chatapp.infrastructure.security

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import java.util.HashMap
import java.util.stream.Collectors.toSet
import java.util.stream.Stream

class KeycloakJwtAuthenticationConverter : Converter<Jwt, AbstractAuthenticationToken>{
    override fun convert(source: Jwt): AbstractAuthenticationToken {
        return JwtAuthenticationToken(
            source,
            Stream.concat(
                JwtGrantedAuthoritiesConverter().convert(source)?.stream()?: Stream.empty(),
            extractResourceRoles(source).stream()).collect(toSet()))
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority?> {
        val resourceAccess = HashMap(jwt.getClaim<Map<*, *>>("resource_access"))

        val eternal = resourceAccess["account"] as Map<String, List<String>>?

        val roles = eternal!!["roles"]!!

        return roles.stream()
            .map<SimpleGrantedAuthority?> { role: String -> SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")) }
            .collect(toSet<SimpleGrantedAuthority?>())
    }
}