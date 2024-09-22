package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.HRDTO;
import com.echo.acknowledgehub.dto.StringResponseDTO;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.CompanyService;
import com.echo.acknowledgehub.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class CompanyController {
    private static final Logger LOGGER = Logger.getLogger(CompanyController.class.getName());
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;
    private final CheckingBean CHECKING_BEAN;

    @GetMapping("/user/get-company/{id}")
    private Company getCompany(@PathVariable Long id) {
        LOGGER.info("Company id : " + id);
        return COMPANY_SERVICE.findById(id).join().get();
    }

    @GetMapping("/user/get-company-dto/{id}")
    private CompanyDTO getCompanyDTO(@PathVariable Long id) {
        LOGGER.info("CompanyDTO id : " + id);
        return COMPANY_SERVICE.findDTOById(id).join();
    }

    @GetMapping("/mr/get-companies")
    public List<Company> getCompanies() {
        return COMPANY_SERVICE.getAll();
    }

    @GetMapping("/mr/get-company-dtos")
    public List<CompanyDTO> getCompanyDTOS() {
        return COMPANY_SERVICE.getAllDTO();
    }

    @GetMapping(value = "/get-departments", produces = MediaType.APPLICATION_JSON_VALUE)

    public List<Department> getDepartments() {
        return DEPARTMENT_SERVICE.getAll();
    }

    @GetMapping(value = "/get-active-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnnouncementCategory> getCategories() {
        return ANNOUNCEMENT_CATEGORY_SERVICE.getActiveCategories();
    }

    @GetMapping(value = "/getCompanyById", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Company> getCompanyById() {
        Optional<Company> optionalCompany = COMPANY_SERVICE.getCompanyById(CHECKING_BEAN.getCompanyId());
        return optionalCompany.map(company -> new ResponseEntity<>(company, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/get-company-name")
    private StringResponseDTO getName() {
        return new StringResponseDTO(COMPANY_SERVICE.getCompanyName(CHECKING_BEAN.getCompanyId()));
    }

    @GetMapping(value = "/hrs/company-count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> countCompany() {
        long count = COMPANY_SERVICE.countCompany();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/mr/company")
    private HRDTO save(@RequestBody HRDTO hrdto){
        return COMPANY_SERVICE.saveHR(hrdto).join();
    }

    @GetMapping("/hrs/company/by-department/{departmentId}")
    private Company getByDepartmentId(@PathVariable("departmentId") Long departmentId){
        return COMPANY_SERVICE.getByDepartmentId(departmentId).join();
    }
}



