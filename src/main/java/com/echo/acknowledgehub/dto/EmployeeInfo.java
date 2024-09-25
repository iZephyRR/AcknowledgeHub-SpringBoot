package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeInfo {
    private Long departmentId;
    private String employeeName;
    private String departmentName;
    private String companyName;
}
