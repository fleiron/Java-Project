package com.ilyabnae.goaltracker.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

// Декодер вхідних JWT для Resource Server: перевіряє підпис і строк дії токена.
// Той самий секрет використовує JwtIssuer для випуску токенів — обидва мають збігатися.
@Configuration
public class JwtDecoderConfig {

	@Bean
	public JwtDecoder jwtDecoder(@Value("${app.security.jwt.secret}") String secret) {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		// HS256 потребує мінімум 256 біт = 32 байти ключа
		if (keyBytes.length < 32) {
			throw new IllegalStateException("JWT secret must be at least 32 UTF-8 bytes (HS256)");
		}
		SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
		return NimbusJwtDecoder.withSecretKey(key).build();
	}

}
