package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

@Service

public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private AnnouncementService(AnnouncementRepository announcementRepository){
        this.announcementRepository=announcementRepository;

    }


}
