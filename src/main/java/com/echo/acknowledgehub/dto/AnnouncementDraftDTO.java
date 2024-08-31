package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.ContentType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Data
public class AnnouncementDraftDTO {
    private Long id;
    private String title;
    private AnnouncementStatus status;
    private MultipartFile file;
    private String filename;
    private String fileUrl;
    private Long categoryId;
    private String target;
    private String categoryName;
    private ContentType contentType;
    private LocalDate draftAt;
}
