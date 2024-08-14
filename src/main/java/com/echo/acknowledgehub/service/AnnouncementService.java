package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.repository.AnnouncementRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class AnnouncementService {
    private static final Logger LOGGER = Logger.getLogger(AnnouncementService.class.getName());
    private final AnnouncementRepository ANNOUNCEMENT_REPOSITORY;
    private final CloudinaryService CLOUD_SERVICE;

    @Async
    public CompletableFuture<Optional<Announcement>> findById(Long id) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<Announcement> save(Announcement announcement, MultipartFile file) throws IOException {
        LOGGER.info("in announcement service");
        handleFileUpload(announcement, file);
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.save(announcement));
    }

    @Async
    public CompletableFuture<List<Announcement>> saveAll(List<Announcement> announcements) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.saveAll(announcements));
    }

    private void handleFileUpload(Announcement announcement, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            Map<String, String> result = CLOUD_SERVICE.upload(file);
            announcement.setPdfLink(result.get("url"));
        }
    }


}
