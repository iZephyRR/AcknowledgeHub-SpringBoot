package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.persistence.constant.EmployeeRole;
import com.echo.acknowledgehub.persistence.constant.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckingDTO {
    private EmployeeStatus status;
    private EmployeeRole role;
}
