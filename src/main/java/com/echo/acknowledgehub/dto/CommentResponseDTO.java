package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentResponseDTO {

    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    private byte[] photoLink;

}
