package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class DepartmentService {
    private static final Logger LOGGER = Logger.getLogger(DepartmentService.class.getName());
    private final DepartmentRepository DEPARTMENT_REPOSITORY;
}
