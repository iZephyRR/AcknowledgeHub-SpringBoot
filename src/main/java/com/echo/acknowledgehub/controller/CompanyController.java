package com.echo.acknowledgehub.controller;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class CompanyController {
    private static final Logger LOGGER = Logger.getLogger(CompanyController.class.getName());
    private final CompanyService COMPANY_SERVICE;
}
