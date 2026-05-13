package com.ilyabnae.goaltracker.api;

import com.ilyabnae.goaltracker.api.dto.LoginRequest;
import com.ilyabnae.goaltracker.api.dto.TokenResponse;
import com.ilyabnae.goaltracker.security.DemoUsersProperties;
import com.ilyabnae.goaltracker.security.DemoUsersProperties.DemoUser;
import com.ilyabnae.goaltracker.security.JwtIssuer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Контролер логіну. Приймає username+password, повертає підписаний JWT.
// @SecurityRequirements (порожній) прибирає глобальну "лочку" Swagger для цього ендпоінта —
// інакше Swagger UI вимагав би токен ще ДО логіну (а ми його якраз і випускаємо).
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Issue JWT access tokens for demo users")
@SecurityRequirements
public class AuthController {

	private final DemoUsersProperties demoUsers;
	private final JwtIssuer jwtIssuer;

	@PostMapping("/login")
	@Operation(summary = "Exchange username/password for a signed JWT (HS256)")
	public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		// 1) знайти демо-користувача за іменем; 2) перевірити пароль; 3) інакше — 401
		DemoUser user = demoUsers
				.findByUsername(request.username())
				.filter(u -> u.getPassword() != null && u.getPassword().equals(request.password()))
				.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

		// JwtIssuer підписує токен HS256 тим самим секретом, який очікує JwtDecoder
		var issued = jwtIssuer.issue(user.getUsername(), user.getDisplayName(), user.resolvedRoles());
		return ResponseEntity.ok(new TokenResponse(issued.accessToken(), "Bearer", issued.expiresIn()));
	}

}
