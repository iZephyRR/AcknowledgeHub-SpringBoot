package com.echo.acknowledgehub.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserExcelDTO {
    private Long companyId;
    private Long departmentId;
    private String departmentName;
    private List<UserDTO> users;
}
