package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
//    private Long id;
    private LocalDateTime noticeAt;
    private LocalDateTime notedAt;
    private NotificationStatus status;
    private NotificationType type;
    private String name;
    private Long userId;
    private Long announcementId;
    private Long targetId;
    private String categoryName;
    private String title;
    private Long sentTo;
    private LocalDateTime timestamp;
    private Long CompanyId;
    private Long employeeId;
    private String employeeName;
    private Long departmentId;
    private String departmentName;



}
