package com.ace.project.oauth2;

import com.ace.project.dto.CustomOAuth2User;
import com.ace.project.entity.Refresh;
import com.ace.project.jwt.JWTUtil;
import com.ace.project.repository.RefreshRepository;
import com.ace.project.service.CustomOAuth2UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);
	
	private final JWTUtil jwtUtil;	
	private final RefreshRepository refreshRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

		//OAuth2User
		CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
	
		String nickname = customUserDetails.getName();
	    
		logger.debug("login success nickname :: " + nickname);
	    
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();
	    
		// 권한이 GUEST라면 회원가입 페이지로 보냄
	    if (role.equals("ROLE_GUEST")) {
	    
	    	String redirect = UriComponentsBuilder.fromUriString("http://localhost:3000/signup")
	    			.queryParam("email", customUserDetails.getEmail())
	    			.queryParam("nickname", nickname)
	    			.build()
	    			.encode(StandardCharsets.UTF_8)
	    			.toUriString();
	    	getRedirectStrategy().sendRedirect(request, response, redirect);
	      
	    	return;
	
	    } else {
	    	
	    	// 토큰 생성
	    	String access = jwtUtil.createJwt("access", nickname, role, 600000L);
	    	String refresh = jwtUtil.createJwt("refresh", nickname, role, 86400000L);

	    	// Refresh 토큰 저장
	    	addRefreshEntity(nickname, refresh, 86400000L);

	    	// 응답
	    	response.setHeader("access", access);
	    	response.addCookie(createCookie("refresh", refresh));

	    	String redirect = UriComponentsBuilder.fromUriString("http://localhost:3000/redirect/")
	    			.queryParam("access", access)
	    			.build().toUriString();
	    	getRedirectStrategy().sendRedirect(request, response, redirect);

	    }
    
	}
	
	
	// 쿠키 생성
	private Cookie createCookie(String key, String value) {
	
		Cookie cookie = new Cookie(key, value);
	    cookie.setMaxAge(24 * 60 * 60);
	    //cookie.setSecure(true);
	    cookie.setPath("/");
	    cookie.setHttpOnly(true);
	    
	    return cookie;
	}

    
	// user당 n개의 Refresh 토큰 발급이 가능
	// 동일한 아이디로 PC, 모바일 등 여러 디바이스에서 로그인 하도록 허용
	private void addRefreshEntity(String nickname, String refresh, Long expiredMs) {
	
	    Date date = new Date(System.currentTimeMillis() + expiredMs);
	
	    Refresh refreshEntity = new Refresh();
	    refreshEntity.setNickname(nickname);
	    refreshEntity.setRefresh(refresh);
	    refreshEntity.setExpiration(date.toString());
	    
	    refreshRepository.save(refreshEntity);
	}
}
