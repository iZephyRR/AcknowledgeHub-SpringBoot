package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.entity.Target;
import com.echo.acknowledgehub.repository.TargetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class TargetService {
    private static final Logger LOGGER = Logger.getLogger(TargetService.class.getName());
    private final TargetRepository TARGET_REPOSITORY;

    public List<Target> insertTarget(List<Target> targetList){
        return TARGET_REPOSITORY.saveAll(targetList);
    }
}
