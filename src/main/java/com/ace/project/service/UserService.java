package com.ace.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ace.project.dto.UserDTO;
import com.ace.project.entity.User;
import com.ace.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	private final UserRepository userRepository;

	// 회원가입
	public User join(UserDTO userDTO) {
		
		logger.debug("UserService :: join");

		UserDTO user = UserDTO.builder()
				.nickname(userDTO.getNickname())
		        .username(userDTO.getUsername())
		        .phone(userDTO.getPhone())
		        .birth(userDTO.getBirth())
		        .gender(userDTO.getGender())
		        .email(userDTO.getEmail())
		        .role("ROLE_USER")
		        .build();
		
		User userEntity = user.toEntity();
		
		return userRepository.save(userEntity);
	}
	
	
	// 닉네임 중복확인
	public boolean checkNickname(String nickname) {
		
		logger.debug("UserService :: 중복확인");
		
		return userRepository.existsByNickname(nickname);
	}

}
