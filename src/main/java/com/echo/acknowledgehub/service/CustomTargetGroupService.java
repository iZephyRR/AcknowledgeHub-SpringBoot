package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.CustomTargetGroupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomTargetGroupService {
    private final CustomTargetGroupRepository customTargetGroupRepository;
}
