package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.ContentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementsForShowing {
    private Long id;
    private String title;
    private ContentType contentType;
    private String pdfLink;
    private String categoryName;
    private String createdBy;
    private LocalDateTime createdAt;

    public AnnouncementsForShowing(Long id, String title, ContentType contentType, String pdfLink,
                                   String categoryName, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
        this.pdfLink = pdfLink;
        this.categoryName= categoryName;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
}