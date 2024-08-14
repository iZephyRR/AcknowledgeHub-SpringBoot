package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.CustomTargetGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CustomTargetGroupService {
    private static final Logger LOGGER = Logger.getLogger(CustomTargetGroupService.class.getName());
    private final CustomTargetGroupRepository CUSTOM_TARGET_GROUP_REPOSITORY;
}
