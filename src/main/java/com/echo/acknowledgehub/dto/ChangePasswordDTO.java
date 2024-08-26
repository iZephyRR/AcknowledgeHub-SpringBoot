package com.echo.acknowledgehub.dto;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String email;
    private String password;
}
