package com.ilyabnae.goaltracker.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
public class JwtRoleConverterConfig {

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		Converter<Jwt, Collection<GrantedAuthority>> rolesConverter = jwt -> {
			Object roles = jwt.getClaim("roles");
			if (roles == null) {
				return Collections.emptyList();
			}
			if (roles instanceof Collection<?> coll) {
				return coll.stream()
						.map(Object::toString)
						.map(String::trim)
						.filter(s -> !s.isEmpty())
						.map(SimpleGrantedAuthority::new)
						.collect(Collectors.toList());
			}
			if (roles instanceof String s && !s.isBlank()) {
				return List.of(new SimpleGrantedAuthority(s.trim()));
			}
			return Collections.emptyList();
		};

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(rolesConverter::convert);
		converter.setPrincipalClaimName("sub");
		return converter;
	}

}
