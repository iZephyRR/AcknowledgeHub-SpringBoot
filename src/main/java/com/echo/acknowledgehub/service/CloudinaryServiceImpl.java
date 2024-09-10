package com.echo.acknowledgehub.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary CLOUDINARY;

    public CloudinaryServiceImpl() {
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("cloud_name", "dnzvk2wdj");
        valuesMap.put("api_key", "255857361962683");
        valuesMap.put("api_secret", "uZd-3RT2bArtozO0cYcHGGbR9bw");
        CLOUDINARY = new Cloudinary(valuesMap);
    }

    @Override
    public Map<String, String> upload(MultipartFile file) throws IOException {
        String folder;
        String format = null; // Default format is null
        Map<String, Object> options;

        // Determine folder based on file type
        String contentType = file.getContentType();
        String originalFileName = removeFileExtension(Objects.requireNonNull(file.getOriginalFilename()));

        if (contentType.startsWith("image/")) {
            folder = "images/";
        } else if (contentType.startsWith("application/pdf")) {
            folder = "pdfs/";
        } else if (contentType.startsWith("application/x-zip-compressed")) {
            folder = "zips/";
            format = "zip"; // Explicitly set format to zip
        } else if (contentType.startsWith("application/vnd.ms-excel") ||
                contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            folder = "excels/";
            format = "xlsx"; // Explicitly set format to xlsx for Excel files
        } else if (contentType.startsWith("video/")) {
            folder = "videos/";
            format = "mp4";
        } else {
            folder = "other/";
        }

        // Prepare the upload options
        if (format != null) {
            options = ObjectUtils.asMap(
                    "resource_type", "raw",  // Use raw resource type for non-image files
                    "folder", folder,
                    "format", format   ,      // Explicitly set format for the file extension
                    "public_id" ,originalFileName
            );
        } else {
            options = ObjectUtils.asMap(
                    "resource_type", "auto",  // Automatically determine the resource type for other files
                    "folder", folder,
                    "public_id" ,originalFileName
            );
        }
        return CLOUDINARY.uploader().upload(file.getBytes(), options);
    }

    @Override
    public Map<String, Object> delete(String publicId) {
        try {
            return CLOUDINARY.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String generateImageUrl(String publicId) {
        return CLOUDINARY.url().generate(publicId);
    }

    private String removeFileExtension(String originalFilename) {
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return originalFilename; // No dot found, return the original filename
        }
        return originalFilename.substring(0, lastDotIndex); // Extract the part before the last dot
    }

}
