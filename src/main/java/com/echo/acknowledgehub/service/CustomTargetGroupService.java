package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.CustomTargetGroupRepository;
import org.springframework.stereotype.Service;

@Service

public class CustomTargetGroupService {
    private final CustomTargetGroupRepository customTargetGroupRepository;
    private CustomTargetGroupService(CustomTargetGroupRepository customTargetGroupRepository){
        this.customTargetGroupRepository=customTargetGroupRepository;

    }
}
