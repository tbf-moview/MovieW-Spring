package com.moview.common;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

	SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	private String secretKey;

	@PostConstruct
	protected void init() {
		// secretKey = Base64.getEncoder().encodeToString(key.getEncoded());
		// secretkey는 후에 properties에 추가
		secretKey = "wKiLdOXjE24YA3h8ETn1p19EnXRjUBMKDbY0sdw/jjA=";
	}

	private long accessTokenValidityInMilliseconds = 3600000; // 1시간 (액세스 토큰 유효 기간)
	private long refreshTokenValidityInMilliseconds = 604800000; // 7일 (리프레시 토큰 유효 기간)


	public String generateAccessToken(String email,String nickname) {
		Map<String, Object> additionalClaims = new HashMap<>();
		additionalClaims.put("nickname", nickname); // 닉네임 추가
		return Jwts.builder()
			.setSubject(email) // email
			.setClaims(additionalClaims)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityInMilliseconds))
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
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
