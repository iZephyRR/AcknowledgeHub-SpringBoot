package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.*;
import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
    private String telegramUsername;
    private String email;
    private String stuffId;
    private String nRC;
    private String name;
    private String password;
    private EmployeeRole role;
    private Gender gender;
    private Date dob;
    private String address;
    private Date workEntryDate;
    private Long departmentId;
    private Long companyId;
}
