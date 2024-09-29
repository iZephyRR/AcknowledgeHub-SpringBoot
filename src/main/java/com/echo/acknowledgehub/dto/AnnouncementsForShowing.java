package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.*;
import lombok.AllArgsConstructor;
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
    private Channel channel;
    private  byte[] photoLink;
    private SelectAll selectAll;
    private ToOwnCompany toOwnCompany;
    private Long announcer;
    private AnnouncementResponseCondition announcementResponseCondition;

    // Constructor that matches the query parameters
    public AnnouncementsForShowing(Long id, String title, ContentType contentType, String pdfLink, String categoryName,
                                   String createdBy, LocalDateTime createdAt, Channel channel,  byte[] photoLink, SelectAll selectAll,Long announcer) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
        this.pdfLink = pdfLink;
        this.categoryName = categoryName;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.channel = channel;
        this.photoLink = photoLink;
        this.selectAll = selectAll;
        this.announcer =announcer;
    }
}