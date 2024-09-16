package com.echo.acknowledgehub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDTO implements Serializable {
    private String address;
    private String subject;
    private String message;
    private MultipartFile file;
}
