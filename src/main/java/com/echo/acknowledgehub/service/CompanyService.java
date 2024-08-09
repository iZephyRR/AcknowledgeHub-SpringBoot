package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.CompanyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CompanyService {
    private static final Logger LOGGER = Logger.getLogger(CompanyService.class.getName());
    private final CompanyRepository COMPANY_REPOSITORY;
}
