package com.ace.project.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ace.project.jwt.JWTFilter;
import com.ace.project.jwt.JWTUtil;
import com.ace.project.oauth2.CustomSuccessHandler;
import com.ace.project.service.CustomOAuth2UserService;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
	
	private final JWTUtil jwtUtil;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomSuccessHandler customSuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

			// login -> 필터만 통과, SecurityConfig 설정으로 진행
			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
				configuration.setAllowedMethods(Collections.singletonList("*"));
				configuration.setAllowCredentials(true);
				configuration.setAllowedHeaders(Collections.singletonList("*"));
				configuration.setMaxAge(3600L);

				configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
				configuration.setExposedHeaders(Collections.singletonList("access"));

				return configuration;
			}
		}));

	    //csrf disable
	    http.csrf((auth) -> auth.disable());
	
	    //From 로그인 방식 disable
	    http.formLogin((auth) -> auth.disable());
	
	    //HTTP Basic 인증 방식 disable
	    http.httpBasic((auth) -> auth.disable());
	    
	    //JWTFilter
		http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
	    
	    //oauth2
		http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
        );
	
		//경로별 인가 작업(일단 전체 열어둠)
		http.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/**").permitAll()
				.anyRequest().authenticated());
	
		//세션 설정 : STATELESS
		http.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	
	    return http.build();
	}
}
