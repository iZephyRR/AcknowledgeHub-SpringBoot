package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.constant.Gender;
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
    private EmployeeStatus status;
    private String address;
    private Date workEntryDate;
    private Long departmentId;
    private String departmentName;
    private Long companyId;
    private String companyName;
}
