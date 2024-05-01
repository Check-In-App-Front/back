package com.ace.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ace.project.dto.CustomOAuth2User;
import com.ace.project.dto.KakaoResponse;
import com.ace.project.dto.OAuth2Response;
import com.ace.project.dto.UserDTO;
import com.ace.project.entity.User;
import com.ace.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
	
	private final UserRepository userRepository;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		logger.debug("oAuth2User :: " + oAuth2User);
        
        
        // 클라이언트 등록 ID 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        logger.debug("Service registrationId :: " + registrationId);
        
        OAuth2Response oAuth2Response = null;
        
        // 등록 ID "kakao"
        if (registrationId.equals("kakao")) {
            // KakaoResponse 객체 생성 -> OAuth2User 속성 전달
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }
        
        // email로 존재하는 user인지 확인
        User existData = userRepository.findByEmail(oAuth2Response.getEmail());
        
        // 없는 user라면
        if (existData == null) {
        	
        	// email, nickname -> dto add (임시)
            UserDTO userDTO = UserDTO.builder()
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getNickName())
                    .role("ROLE_GUEST")
                    .build();

            return new CustomOAuth2User(userDTO);
        } else {
        	
        	// 존재하는 user
            UserDTO userDTO = UserDTO.builder()
                    .nickname(existData.getNickname())
                    .username(existData.getUsername())
                    .phone(existData.getPhone())
                    .birth(existData.getBirth())
                    .gender(existData.getGender())
                    .email(existData.getEmail())
                    .role(existData.getRole())
                    .build();

            return new CustomOAuth2User(userDTO);
        }
		
	}
}
