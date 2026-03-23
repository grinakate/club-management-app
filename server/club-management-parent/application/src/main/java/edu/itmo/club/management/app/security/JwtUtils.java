package edu.itmo.club.management.app.security;

import edu.itmo.club.management.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@AllArgsConstructor
public class JwtUtils {

	private final JwtProperties jwtProperties;

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(User user) {
		return Jwts.builder()
				.subject(user.getId().toString())
				.claim("id", user.getId())
				.claim("role", user.getRole())
				.issuedAt(new Date())
				.expiration(new Date((new Date()).getTime() + jwtProperties.getExpirationMs()))
				.signWith(getSigningKey(), Jwts.SIG.HS256)
				.compact();
	}

	public String getUserIdFromToken(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build()
				.parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

