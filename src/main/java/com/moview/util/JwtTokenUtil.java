package com.moview.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenUtil {

	SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	private String secretKey;

	@PostConstruct
	protected void init() {
		// secretKey = Base64.getEncoder().encodeToString(key.getEncoded()); // 시크릿 키 생성 (한 번 하고 저장)
		// secretkey는 후에 properties에 추가
		secretKey = "wKiLdOXjE24YA3h8ETn1p19EnXRjUBMKDbY0sdw/jjA=";
	}

	private long accessTokenValidityInMilliseconds = 3600000; // 1시간 (액세스 토큰 유효 기간)
	private long refreshTokenValidityInMilliseconds = 604800000; // 7일 (리프레시 토큰 유효 기간)


	public String generateAccessToken(String email,String nickname) {
		Map<String, Object> additionalClaims = new HashMap<>();
		additionalClaims.put("nickname", nickname); // 닉네임 추가
		additionalClaims.put("email", email); // 닉네임 추가
		return Jwts.builder()
			.setClaims(additionalClaims)
			.setSubject(email) // email
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityInMilliseconds))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public String extractUserEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractNickname(String token) {
		return extractClaim(token, claims -> claims.get("nickname", String.class));
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = decodeJwt(token);
		return claimsResolver.apply(claims);
	}


	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// 리프레시 토큰 생성

	public String generateRefreshToken(String email) {
		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidityInMilliseconds))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
	}

	public Claims decodeJwt(String token) {
		try {
			// JWT를 복호화하여 Claims 객체로 반환
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (Exception e) {
			// 복호화 실패 시 예외 처리
			throw new RuntimeException("JWT decoding failed", e);
		}
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
