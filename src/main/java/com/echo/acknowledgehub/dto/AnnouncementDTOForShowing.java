package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.constant.ContentType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AnnouncementDTOForShowing {
    private Long id;
    private String title;
    private ContentType contentType;
    private String pdfLink;

    public AnnouncementDTOForShowing(Long id, String title, ContentType contentType, String pdfLink) {
        this.id = id;
        this.title = title;
        this.contentType = contentType;
        this.pdfLink = pdfLink;
    }

}
