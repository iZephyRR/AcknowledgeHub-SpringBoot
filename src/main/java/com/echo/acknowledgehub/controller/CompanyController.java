package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.CompanyService;
import com.echo.acknowledgehub.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}/mr")
@AllArgsConstructor
public class CompanyController {
    private static final Logger LOGGER = Logger.getLogger(CompanyController.class.getName());
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;

    @GetMapping("get-company")
    private CompletableFuture<Optional<Company>> getCompany(@RequestBody String id){
        LOGGER.info("Company id : "+id);
        return COMPANY_SERVICE.findById(Long.parseLong(id));
    }

    @GetMapping(value = "get-companies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Company> getCompanies () {
        return COMPANY_SERVICE.getAllCompanies();
    }

    @GetMapping(value = "get-departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Department> getDepartments () {
        return DEPARTMENT_SERVICE.getAll();
    }

    @GetMapping(value = "get-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnnouncementCategory> getCategories () {
        return ANNOUNCEMENT_CATEGORY_SERVICE.findAll();
    }


}
