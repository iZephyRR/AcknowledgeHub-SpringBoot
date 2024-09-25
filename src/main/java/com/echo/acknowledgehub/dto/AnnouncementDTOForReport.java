package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnnouncementDTOForReport {
    private Long id;
    private String title;
    private String categoryName;
    private ContentType contentType;
    private LocalDateTime createdAt;
    private Channel channel;
    private String createdBy;
    private EmployeeRole role;
    private String companyName;
    private SelectAll selectAll;
    private AnnouncementStatus status;
}
