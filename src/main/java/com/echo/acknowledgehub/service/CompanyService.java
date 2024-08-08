package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service

public class CompanyService {
    private final CompanyRepository companyRepository;
    private CompanyService(CompanyRepository companyRepository){
        this.companyRepository=companyRepository;

    }
}
