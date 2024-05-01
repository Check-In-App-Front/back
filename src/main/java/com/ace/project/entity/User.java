package com.ace.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(unique = true)
	@NotNull
    private String nickname;
    
    private String username; 

    private String phone;

    private String birth;

    private String gender;
    
    @Column(unique = true)
    @NotNull
    private String email;

    private String role;
    
    @Builder
    public User(Long id, String nickname, String username, String phone, String birth, String gender,
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



}
