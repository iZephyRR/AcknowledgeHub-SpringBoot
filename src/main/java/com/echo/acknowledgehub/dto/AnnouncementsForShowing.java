package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.AnnouncementResponseCondition;
import com.echo.acknowledgehub.constant.Channel;
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
    private Channel channel;
    private AnnouncementResponseCondition announcementResponseCondition;

    public AnnouncementsForShowing(Long id, String title, ContentType contentType, String pdfLink,
                                   String categoryName, String createdBy, LocalDateTime createdAt, Channel channel) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
        this.pdfLink = pdfLink;
        this.categoryName= categoryName;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.channel = channel;
    }
}