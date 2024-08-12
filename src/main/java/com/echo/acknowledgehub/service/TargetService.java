package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.TargetRepository;
import com.echo.acknowledgehub.util.XlsxReader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class TargetService {
    private static final Logger LOGGER = Logger.getLogger(TargetService.class.getName());
    private final TargetRepository TARGET_REPOSITORY;
}
