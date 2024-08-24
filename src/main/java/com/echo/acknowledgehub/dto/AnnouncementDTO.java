package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private Long id;
    private String title;
    private MultipartFile file;
    private String filename;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private LocalDateTime createdAt;
    private String scheduleOption;
    private Long categoryId;
    private String categoryName;
    private String createdBy;
    private AnnouncementStatus status;
    private String target;
}
