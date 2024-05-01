package com.ace.project.dto;

public interface OAuth2Response {
    //제공자
    String getProvider();
    
    //제공자에서 발급해주는 아이디
    String getProviderId();
    
    //이메일
    String getEmail();
    
    //사용자 닉네임
    String getNickName();
}
