package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.persistence.entity.Announcement;
import com.echo.acknowledgehub.persistence.repository.AnnouncementRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class AnnouncementService {
    private static final Logger LOGGER = Logger.getLogger(AnnouncementService.class.getName());
    private final AnnouncementRepository ANNOUNCEMENT_REPOSITORY;

    @Async
    public CompletableFuture<Optional<Announcement>> findById(Long id) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<Announcement> save(Announcement announcement) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.save(announcement));
    }

    @Async
    public CompletableFuture<List<Announcement>> saveAll(List<Announcement> announcements) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.saveAll(announcements));
    }


}
