package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.TargetRepository;
import org.springframework.stereotype.Service;

@Service

public class TargetService {
    private final TargetRepository targetRepository;
    private TargetService(TargetRepository targetRepository){
        this.targetRepository=targetRepository;

    }
}
