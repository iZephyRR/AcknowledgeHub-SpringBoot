package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentsForShowing {
    private String content;
    private LocalDateTime createdAt;
    private String authorName;
}
