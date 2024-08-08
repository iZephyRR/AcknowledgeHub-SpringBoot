package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementCategoryRepository;
import com.echo.acknowledgehub.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

@Service

public class AnnouncementCategoryService {
    private final AnnouncementCategoryRepository announcementCategoryRepository;
    private AnnouncementCategoryService(AnnouncementCategoryRepository announcementCategoryRepository){
        this.announcementCategoryRepository=announcementCategoryRepository;

    }
}
