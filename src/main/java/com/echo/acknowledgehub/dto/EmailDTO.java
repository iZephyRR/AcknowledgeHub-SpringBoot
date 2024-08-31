package com.echo.acknowledgehub.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class EmailDTO implements Serializable {
    private String address;
    private String subject;
    private String message;
    private MultipartFile file;
}
