package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service

public class NotificationService {
    private final NotificationRepository notificationRepository;
    private NotificationService(NotificationRepository notificationRepository){
        this.notificationRepository=notificationRepository;

    }
}
