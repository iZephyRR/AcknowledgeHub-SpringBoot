package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
//    private Long id;
    private LocalDateTime noticeAt;
    private NotificationStatus status;
    private NotificationType type;
    private Long employeeId;
    private Long announcementId;
    private Long targetId;
    private Long categoryId;
    private String title;
//    private String receiverType;
    private Long sentTo;
//    private Long companyId;
}
