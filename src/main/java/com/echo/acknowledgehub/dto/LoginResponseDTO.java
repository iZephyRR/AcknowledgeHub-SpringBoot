package com.echo.acknowledgehub.dto;

import lombok.Getter;

@Getter
public class LoginResponseDTO {
    private final String LOGIN_RESPONSE;
    public LoginResponseDTO(String loginResponse){
        this.LOGIN_RESPONSE=loginResponse;
    }
}
