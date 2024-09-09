package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CompanyService {
    private static final Logger LOGGER = Logger.getLogger(CompanyService.class.getName());
    private final CompanyRepository COMPANY_REPOSITORY;

    @Async
    public CompletableFuture<Optional<Company>> findById(Long id){
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<Company> save(Company company){
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.save(company));
    }

    @Async
    public CompletableFuture<Optional<Company>> findByName(String name){
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.findByName(name));
    }

    public List<Company> getAllCompanies () {
        return COMPANY_REPOSITORY.findAll();
    }

    public Optional<Company> getCompanyById(Long id){
        return COMPANY_REPOSITORY.findById(id);
    }

    public boolean existsById(Long sendTo) {
        return COMPANY_REPOSITORY.existsById(sendTo);
    }

    @Transactional
    public String getCompanyName(Long compayId) {
        return COMPANY_REPOSITORY.findCompanyNameById(compayId);
    }
}
