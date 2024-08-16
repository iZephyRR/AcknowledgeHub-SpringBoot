package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class CompanyController {
    private static final Logger LOGGER = Logger.getLogger(CompanyController.class.getName());
    private final CompanyService COMPANY_SERVICE;

    @GetMapping("get-company")
    private CompletableFuture<Optional<Company>> getCompany(@RequestBody String id){
        LOGGER.info("Company id : "+id);
        return COMPANY_SERVICE.findById(Long.parseLong(id));
    }
}
