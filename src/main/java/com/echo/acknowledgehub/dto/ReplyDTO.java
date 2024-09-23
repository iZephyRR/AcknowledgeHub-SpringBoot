package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ReplyDTO {
    private String content;
    private Long commentId;
}
