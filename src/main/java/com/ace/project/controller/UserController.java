package com.ace.project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.ace.project.dto.UserDTO;
import com.ace.project.entity.User;
import com.ace.project.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@Operation(summary = "회원가입")
	@PostMapping("/signUp")
	public ResponseEntity<?> join(@RequestBody UserDTO userDTO) {
		User user = userService.join(userDTO);

	    if (user != null) {
	      return ResponseEntity.status(HttpStatus.CREATED).body(user);
	    } else {
	      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 생성 실패");
	    }
	 }
	
	@Operation(summary = "닉네임 중복 확인")
	@GetMapping("/isExist/{nickname}")
	public ResponseEntity<Boolean> checkNickname(@PathVariable String nickname) {
		boolean isDuplicate  = userService.checkNickname(nickname);
		
		return ResponseEntity.ok(isDuplicate);
	}

}
