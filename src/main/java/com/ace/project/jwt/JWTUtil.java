package com.ace.project.jwt;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import java.util.Date;

@Component
public class JWTUtil {
	private SecretKey secretKey;
	
	public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }
	
	// 토큰 종류 확인
    public String getCategory(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category",
                String.class);
    }
    
    // nickname 확인
    public String getNickname(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("nickname", String.class);
    }

    // role 확인
    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
    
    // 토큰 만료 확인
    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }
    
    
    // 토큰 생성
    public String createJwt(String category, String nickname, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("category", category)
                .claim("nickname", nickname)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}
