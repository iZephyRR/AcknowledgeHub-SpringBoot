package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileDTO {
    private Employee employee;
    private Long companyId;
    private String companyName;
    private Long departmentId;
    private String departmentName;

}