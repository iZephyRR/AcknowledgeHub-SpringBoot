package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TargetCompany {
    private String companyName;
    private List<TargetDepartment> departments;
}
