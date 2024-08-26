package com.echo.acknowledgehub.dto;

import lombok.Getter;

@Getter
public class StringResponseDTO {
    private final String STRING_RESPONSE;
    public StringResponseDTO(String stringResponse){
        this.STRING_RESPONSE =stringResponse;
    }
}
