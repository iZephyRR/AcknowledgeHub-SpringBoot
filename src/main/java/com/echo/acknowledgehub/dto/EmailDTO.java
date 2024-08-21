package com.echo.acknowledgehub.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailDTO implements Serializable {
    private String address;
    private String subject;
    private String message;
}
