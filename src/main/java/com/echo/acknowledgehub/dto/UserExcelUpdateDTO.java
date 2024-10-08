package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.constant.Gender;
import lombok.Data;

import java.util.Date;

@Data
public class UserExcelUpdateDTO {
    private Long id;
    private String telegramUsername;
    private String email;
    private String staffId;
    private String nrc;
    private String name;
    private EmployeeRole role;
    private Gender gender;
    private Date dob;
    private EmployeeStatus status;
    private String address;
    private Long departmentId;
    private Long telegramUserId;
    private int notedCount;
}
