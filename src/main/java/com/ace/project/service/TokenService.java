package com.ace.project.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ace.project.entity.Refresh;
import com.ace.project.jwt.JWTUtil;
import com.ace.project.repository.RefreshRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
	
	private final JWTUtil jwtUtil;
	private final RefreshRepository refreshRepository;
	
	// Refresh로 Access 토큰 재발급
	public ResponseEntity<?> tokenReissue(HttpServletRequest request, HttpServletResponse response) {
		
		logger.debug("TokenService :: tokenReissue");
		
		// get refresh token
	    String refresh = null;
	    
	    // 모든 쿠키 값 가져오기
	    Cookie[] cookies = request.getCookies();
	    for (Cookie cookie : cookies) {
	    	
	    	if (cookie.getName().equals("refresh")) {

	    		refresh = cookie.getValue();
	    	}
	    	
	    }
	    
	    if (refresh == null) {
	    	// response status code
	        return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
	    }
	    
	    // expired check
	    try {
	    	jwtUtil.isExpired(refresh);
	    } catch (ExpiredJwtException e) {
	    	// response status code
	        return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
		}
	    
	    // 토큰이 refresh인지 확인
	    String category = jwtUtil.getCategory(refresh);
	    
	    if(!category.equals("refresh")) {
	    	// response status code
	    	return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);

	    }
	    
	    // DB에 저장되어 있는지 확인
	    Boolean isExist = refreshRepository.existsByRefresh(refresh);
	    
	    if (!isExist) {
	    	// response body
	        return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
	    }
	    
	    // 닉네임, 권한 가져오기
	    String nickname = jwtUtil.getNickname(refresh);
	    String role = jwtUtil.getRole(refresh);
	    
	    // make new JWT
	    String newAccess = jwtUtil.createJwt("access", nickname, role, 600000L);

	    // Access 토큰 갱신 시 Refresh 토큰도 같이 갱신, 쿠키로 응답
	    String newRefresh = jwtUtil.createJwt("refresh", nickname, role, 86400000L);
	    
	    
	    // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
	    refreshRepository.deleteByRefresh(refresh);
	    addRefreshEntity(nickname, newRefresh, 86400000L);
	    
	    
	    // response
	    response.setHeader("access", newAccess);
	    response.addCookie(createCookie("refresh", newRefresh));

	    return new ResponseEntity<>(HttpStatus.OK);

	}
	
	// 토큰 저장소에 Refresh 토큰 저장
	private void addRefreshEntity(String nickname, String refresh, Long expiredMs) {

		Date date = new Date(System.currentTimeMillis() + expiredMs);

	    Refresh refreshEntity = new Refresh();
	    refreshEntity.setNickname(nickname);
	    refreshEntity.setRefresh(refresh);
	    refreshEntity.setExpiration(date.toString());

	    refreshRepository.save(refreshEntity);
	}
	
	// 쿠키 생성
	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
	    cookie.setMaxAge(24 * 60 * 60);
	    // cookie.setSecure(true);
	    // cookie.setPath("/");
	    cookie.setHttpOnly(true);

	    return cookie;
	}

}
