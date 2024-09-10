package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.constant.ReceiverType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {
    //    private Long id;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime noticeAt;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime notedAt;
    private NotificationType type;
    private String name;
    private Long userId;
    private Long announcementId;
    private Long targetId;
    private String targetName;
    private String categoryName;
    private String title;
    private Long sentTo;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
    private Long CompanyId;
    private Long employeeId;
    private String employeeName;
    private Long departmentId;
    private String departmentName;
    private ReceiverType receiverType;
    private Long receiverId;
    private String status;


}
