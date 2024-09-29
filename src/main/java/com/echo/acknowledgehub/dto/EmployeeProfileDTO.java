package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.Gender;
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
    private Gender gender;
    private String staffId;
    private String companyName;
    private String departmentName;
    private byte[] photoLink;

    EmployeeProfileDTO (String name, EmployeeRole role, String email,Gender gender, String staffId, byte[] photoLink){
        this.name=name;
        this.role=role;
        this.email=email;
        this.gender=gender;
        this.staffId=staffId;
        this.photoLink=photoLink;
    }
}