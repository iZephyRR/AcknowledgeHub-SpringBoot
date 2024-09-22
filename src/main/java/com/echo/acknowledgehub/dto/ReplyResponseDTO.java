package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReplyResponseDTO {
    private Long id;
    private String replierName;
    private String replyContent;
    private LocalDateTime replyCreatedAt;
    private byte[] replierPhotoLink;

}
