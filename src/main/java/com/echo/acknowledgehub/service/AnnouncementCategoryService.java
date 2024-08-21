package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.AnnouncementCategoryStatus;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.repository.AnnouncementCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    @Transactional
    public CompletableFuture<Integer> softDelete(Long id){
        LOGGER.info("softDeleteService : "+id);
        int category=ANNOUNCEMENT_CATEGORY_REPOSITORY.softDeleteById(id, AnnouncementCategoryStatus.SOFT_DELETE);
        LOGGER.info("SoftDelete : "+category);
        return CompletableFuture.completedFuture(ANNOUNCEMENT_CATEGORY_REPOSITORY.softDeleteById(id, AnnouncementCategoryStatus.SOFT_DELETE));
    }

    @Async
    @Transactional
    public CompletableFuture<Integer> softUndelete(Long id) {
        LOGGER.info("softUndeleteService : " + id);
        int category = ANNOUNCEMENT_CATEGORY_REPOSITORY.softDeleteById(id, AnnouncementCategoryStatus.ACTIVE);
        LOGGER.info("SoftUndelete : " + category);
        return CompletableFuture.completedFuture(category);
    }

    public List<AnnouncementCategory> getAllCategoriesDESC(){
        return ANNOUNCEMENT_CATEGORY_REPOSITORY.getAllCategories();
    }

    public Optional<AnnouncementCategory> findById(Long id){
        return ANNOUNCEMENT_CATEGORY_REPOSITORY.findById(id);
    }
}
