package com.echo.acknowledgehub.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface CloudinaryService {
    Map<String, String> upload(MultipartFile multipartFile) throws IOException;

    Map<String, Object> delete(String publicId) throws IOException;

    String generateImageUrl(String publicId);
}
