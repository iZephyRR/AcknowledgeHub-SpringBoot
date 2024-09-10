package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data

public class EmployeeNotedDTO {
    private Long id;
    private String name;
    private Gender gender; // Ensure Gender is an enum or class
    private EmployeeRole role; // Ensure EmployeeRole is an enum or class
    private EmployeeStatus status; // Ensure EmployeeStatus is an enum or class
    private String staffId;
    private String companyName;
    private String departmentName;
    private LocalDateTime notedAt;

    public EmployeeNotedDTO(Long id, String name, Gender gender, EmployeeRole role,
                            EmployeeStatus status, String staffId, String companyName, String departmentName) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.role = role;
        this.status = status;
        this.staffId = staffId;
        this.companyName = companyName;
        this.departmentName = departmentName;
    }

}
