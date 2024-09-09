package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.ContentType;
import com.echo.acknowledgehub.constant.IsSchedule;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.constant.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private Long id;
    private String title;
    @Nullable
    private MultipartFile file;
    private String fileUrl;
    private String filename;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private String scheduleOption;
    private Long categoryId;
    private String categoryName;
    private String createdBy;
    private AnnouncementStatus status;
    private String target;
    private IsSchedule isSchedule;
    private ContentType contentType;
    private EmployeeRole role;
    private String channel;
    private boolean selectAll;
}

