package com.echo.acknowledgehub.exception_handler;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private final String status = "error";
    private int errorCode;
    private String message;
}