package com.echo.acknowledgehub.dto;

import lombok.Getter;

@Getter
public class JWTToken {
    private final String JWT_TOKEN;
    public JWTToken(String token){
        this.JWT_TOKEN=token;
    }
}
