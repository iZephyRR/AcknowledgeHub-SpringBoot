package com.echo.acknowledgehub.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VersionDTO {
    private String title;
    private MultipartFile file;
    private String filename;
    private String isEmailSelected;
    private LocalDateTime deadline;
    private Long oldVersion;
}
