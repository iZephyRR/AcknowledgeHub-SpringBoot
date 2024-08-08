package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.TargetRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TargetService {
    private final TargetRepository targetRepository;
}
