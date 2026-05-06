package com.ilyabnae.goaltracker.service;

import com.ilyabnae.goaltracker.domain.AppUser;
import com.ilyabnae.goaltracker.repository.AppUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAccountService {

	private final AppUserRepository userRepository;

	@Transactional(readOnly = true)
	public AppUser requireCurrentUser() {
		return resolveCurrentUser().orElseThrow(() -> new IllegalStateException("Unauthenticated request"));
	}

	@Transactional
	public AppUser getOrCreateCurrentUser() {
		Jwt jwt = requireJwt();
		String subject = jwt.getSubject();
		Optional<AppUser> existing = userRepository.findByExternalSubject(subject);
		if (existing.isPresent()) {
			return existing.get();
		}
		String display = firstNonEmpty(
				jwt.getClaimAsString("name"),
				jwt.getClaimAsString("preferred_username"),
				jwt.getClaimAsString("email"),
				subject);
		return userRepository.save(new AppUser(subject, display));
	}

	private Optional<AppUser> resolveCurrentUser() {
		return requireJwtOpt().flatMap(jwt -> userRepository.findByExternalSubject(jwt.getSubject()));
	}

	private static String firstNonEmpty(String... values) {
		if (values == null) {
			return null;
		}
		for (String v : values) {
			if (v != null && !v.isBlank()) {
				return v;
			}
		}
		return null;
	}

	private static Optional<Jwt> requireJwtOpt() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
			return Optional.empty();
		}
		if (auth.getPrincipal() instanceof Jwt jwt) {
			return Optional.of(jwt);
		}
		return Optional.empty();
	}

	private static Jwt requireJwt() {
		return requireJwtOpt().orElseThrow(() -> new IllegalStateException("Expected JWT in security context"));
	}

}
