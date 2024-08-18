package com.echo.acknowledgehub.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl() {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", "dnzvk2wdj");
        valuesMap.put("api_key", "255857361962683");
        valuesMap.put("api_secret", "uZd-3RT2bArtozO0cYcHGGbR9bw");
        cloudinary = new Cloudinary(valuesMap);
    }

    @Override
    public Map<String, String> upload(MultipartFile file) throws IOException {

        String folder = file.getContentType().startsWith("image/") ? "images/" : "pdfs/";
        Map<String, Object> options = ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", folder
        );

        return cloudinary.uploader().upload(file.getBytes(), options);
    }

    @Override
    public Map<String, Object> delete(String publicId) {
        try {
            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String generateImageUrl(String publicId) {
        return cloudinary.url().generate(publicId);
    }
}

