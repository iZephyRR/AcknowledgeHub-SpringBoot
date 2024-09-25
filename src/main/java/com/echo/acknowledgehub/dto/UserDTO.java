package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  
    private Long id;
    private String telegramUsername;
    private String email;
    private String staffId;
    private String nrc;
    private String name;
    private String password;
    private EmployeeRole role;
    private Gender gender;
    private Date dob;
    private EmployeeStatus status;
    private String address;
    private byte[] photoLink;
    private Long departmentId;
    private String departmentName;
    private Long companyId;
    private String companyName;
    private int notedCount;
}
