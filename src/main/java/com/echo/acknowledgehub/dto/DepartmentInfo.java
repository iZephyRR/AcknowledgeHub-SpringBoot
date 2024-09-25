package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DepartmentInfo {
    private Long companyId;
    private String departmentName;
    private String companyName;
}
