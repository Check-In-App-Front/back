package com.ace.project.dto;

import com.ace.project.entity.User;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class UserDTO {
private Long   id;
    
	@Size(max=12, message="최대 12글자까지 입력 가능합니다.")
    private String nickname;
    
	@Size(max=12, message="최대 12글자까지 입력 가능합니다.")
    private String username;
    
    @Pattern(regexp="[0-9]+", message="숫자만 입력 가능합니다.")
    @Size(max=11, message="최대 11글자까지 입력 가능합니다.")
    private String phone;

    private String birth;

    private String gender;
    
    @Pattern(regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$", message = "유효하지 않은 이메일 형식입니다.")
    @Size(max=32, message="최대 32글자까지 입력 가능합니다.")
    private String email;

    private String role;

    @Builder
    public UserDTO(Long id, String nickname, String username, String phone, String birth, String gender,
            String email, String role) {
        this.id = id;
        this.nickname = nickname;
        this.username = username;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;
        this.email = email;
        this.role = role;
    }

    public User toEntity() {
        return User.builder()
                .id(id)
                .nickname(nickname)
                .username(username)
                .phone(phone)
                .birth(birth)
                .gender(gender)
                .email(email)
                .role(role)
                .build();

    }

}
