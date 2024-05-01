package com.ace.project.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ace.project.service.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TokenController {
	
	private final TokenService tokenService;
	
	// Refresh 토큰을 받아 access, refresh 토큰 재발급
	@Operation(summary = "토큰 재발급", description = "Access 만료 -> Refresh를 통해 Access, Refresh 토큰 재발급")
	@PostMapping("/reissue")
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
		return tokenService.tokenReissue(request, response);
	}
}
