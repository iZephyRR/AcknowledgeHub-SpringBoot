package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileDTO {
    private String name;
    private EmployeeRole role;
    private String email;
    private String companyName;
    private String departmentName;
    private byte[] photoLink;

    EmployeeProfileDTO (String name, EmployeeRole role, String email){
        this.name=name;
        this.role=role;
        this.email=email;
    }
}