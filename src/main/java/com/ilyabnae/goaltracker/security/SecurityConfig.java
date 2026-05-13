package com.ilyabnae.goaltracker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

// Головна конфігурація безпеки: визначає, які URL відкриті, а які — лише з валідним JWT.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter)
			throws Exception {
		http
				// CSRF не потрібен для stateless REST API (немає cookie-сесій)
				.csrf(csrf -> csrf.disable())
				// дозволяємо H2-консолі рендеритись у <iframe> того ж origin
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
				// сесії не використовуємо — кожен запит окремо проходить аутентифікацію по JWT
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/actuator/health",
								"/h2-console/**",
								"/api/v1/auth/**")
						.permitAll()
						.requestMatchers("/api/v1/admin/**")
						.hasAuthority("ROLE_ADMIN")
						.anyRequest()
						.authenticated())
				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
		return http.build();
	}

}
