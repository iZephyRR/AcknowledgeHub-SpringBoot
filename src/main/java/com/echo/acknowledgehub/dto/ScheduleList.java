package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.ContentType;
import com.echo.acknowledgehub.constant.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ScheduleList {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private ContentType contentType;
    private EmployeeRole role;
}
