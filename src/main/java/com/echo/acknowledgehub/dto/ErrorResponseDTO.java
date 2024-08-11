package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class ErrorResponseDTO {
    private final String status = "error";
    private int errorCode;
    private String message;

}