package com.echo.acknowledgehub.dto;

import lombok.Getter;

@Getter
public class BooleanResponseDTO {

    private final boolean BOOLEAN_RESPONSE;

    public BooleanResponseDTO(boolean booleanResponse) {
        this.BOOLEAN_RESPONSE = booleanResponse;
    }
}

