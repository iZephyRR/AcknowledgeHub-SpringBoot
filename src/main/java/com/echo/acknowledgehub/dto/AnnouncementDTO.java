package com.echo.acknowledgehub.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private Long id;
    private String title;
    private MultipartFile file;
    private String filename;
    private LocalDateTime createdAt;
    private Long categoryId;
    private String categoryName;
    private String createdBy;
    private String status;
}
