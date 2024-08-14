package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.persistence.constant.AnnouncementCategoryStatus;
import com.echo.acknowledgehub.persistence.entity.AnnouncementCategory;
import com.echo.acknowledgehub.persistence.repository.AnnouncementCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class AnnouncementCategoryService {
    private static final Logger LOGGER = Logger.getLogger(AnnouncementCategoryService.class.getName());
    private final AnnouncementCategoryRepository ANNOUNCEMENT_CATEGORY_REPOSITORY;

    @Async
    public CompletableFuture<AnnouncementCategory> save(AnnouncementCategory announcementCategory){
        return CompletableFuture.completedFuture(ANNOUNCEMENT_CATEGORY_REPOSITORY.save(announcementCategory));
    }

    @Async
    public CompletableFuture<Integer> softDelete(Long id){
        return CompletableFuture.completedFuture(ANNOUNCEMENT_CATEGORY_REPOSITORY.softDeleteById(id, AnnouncementCategoryStatus.SOFT_DELETE));
    }

    @Async
    public CompletableFuture<List<AnnouncementCategory>> findAll(){
        return CompletableFuture.completedFuture(ANNOUNCEMENT_CATEGORY_REPOSITORY.findAll());
    }
}
