package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class AnnouncementCategoryService {
    private static final Logger LOGGER = Logger.getLogger(AnnouncementCategoryService.class.getName());
    private final AnnouncementCategoryRepository ANNOUNCEMENT_CATEGORY_REPOSITORY;
}
