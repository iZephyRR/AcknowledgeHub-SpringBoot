package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnouncementsShowInDashboard {
    private Long id;
    private String title;
    private String categoryName;
    private String createdBy;
    private EmployeeRole role;
    private LocalDateTime createdAt;
}
