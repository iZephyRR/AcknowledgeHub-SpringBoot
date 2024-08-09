package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class AnnouncementService {
    private static final Logger LOGGER = Logger.getLogger(AnnouncementService.class.getName());
    private final AnnouncementRepository ANNOUNCEMENT_REPOSITORY;

}
