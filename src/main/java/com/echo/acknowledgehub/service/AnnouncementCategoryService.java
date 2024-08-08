package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AnnouncementCategoryService {
    private final AnnouncementCategoryRepository announcementCategoryRepository;
}
