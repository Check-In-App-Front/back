package com.ace.project.jwt;


import java.io.PrintWriter;
import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ace.project.dto.CustomOAuth2User;
import com.ace.project.dto.UserDTO;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
	
	private final JWTUtil jwtUtil;
    
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("access");

        System.out.println("jwtUtil token :: " + accessToken);

        // 토큰이 없다면
        if (accessToken == null) {

            filterChain.doFilter(request, response);

            return;
        }


        // 토큰 만료 여부 확인
        try {
        	
            jwtUtil.isExpired(accessToken);
            
        } catch (ExpiredJwtException e) {

            // response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            return;
        }

        // 토큰이 access인지 확인 
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            // response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰에서 nickname과 role 획득
        String nickname = jwtUtil.getNickname(accessToken);
        String role = jwtUtil.getRole(accessToken);

        UserDTO userDTO = UserDTO.builder()
                .nickname(nickname)
                .role(role)
                .build();

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
	
}
