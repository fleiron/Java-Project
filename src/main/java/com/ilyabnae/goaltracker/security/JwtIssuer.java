package com.ilyabnae.goaltracker.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Створює (підписує) JWT за алгоритмом HS256. Використовується AuthController.login.
@Component
public class JwtIssuer {

	private final JWSSigner signer;
	private final long ttlSeconds;

	public JwtIssuer(
			@Value("${app.security.jwt.secret}") String secret,
			@Value("${app.security.jwt.ttl-seconds:3600}") long ttlSeconds)
			throws JOSEException {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			throw new IllegalStateException("JWT secret must be at least 32 UTF-8 bytes (HS256)");
		}
		// MACSigner = HMAC-підпис симетричним секретом
		this.signer = new MACSigner(keyBytes);
		this.ttlSeconds = ttlSeconds;
	}

	// Збираємо набір claim'ів і підписуємо їх. Повертаємо токен + час життя в секундах.
	public IssuedToken issue(String subject, String displayName, List<String> roles) {
		try {
			Instant now = Instant.now();
			Instant exp = now.plusSeconds(ttlSeconds);
			JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
					.subject(subject)              // sub — ідентифікатор користувача
					.issueTime(Date.from(now))     // iat — коли видано
					.expirationTime(Date.from(exp));// exp — коли закінчиться
			if (displayName != null && !displayName.isBlank()) {
				claims.claim("name", displayName);
			}
			if (roles != null && !roles.isEmpty()) {
				claims.claim("roles", roles);
			}
			SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims.build());
			jwt.sign(signer);
			return new IssuedToken(jwt.serialize(), ttlSeconds);
		} catch (JOSEException e) {
			throw new IllegalStateException("Failed to sign JWT", e);
		}
	}

	public record IssuedToken(String accessToken, long expiresIn) {
	}

}
