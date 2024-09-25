package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TargetDepartment {
    private String departmentName;
    private List<TargetEmployee> employees;
}
