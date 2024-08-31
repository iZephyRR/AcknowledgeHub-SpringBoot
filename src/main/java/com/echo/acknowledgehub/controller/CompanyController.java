package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.CompanyService;
import com.echo.acknowledgehub.service.DepartmentService;
import com.echo.acknowledgehub.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class CompanyController {
    private static final Logger LOGGER = Logger.getLogger(CompanyController.class.getName());
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final ModelMapper MODEL_MAPPER;

    @GetMapping("/mr/get-company")
    private CompletableFuture<Optional<Company>> getCompany(@RequestBody String id){
        LOGGER.info("Company id : "+id);
        return COMPANY_SERVICE.findById(Long.parseLong(id));
    }

    @GetMapping(value = "/get-companies", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Company> getCompanies () {
        return COMPANY_SERVICE.getAllCompanies();
    }

    @GetMapping(value = "/get-departments", produces = MediaType.APPLICATION_JSON_VALUE)

    public List<Department> getDepartments () {
        return DEPARTMENT_SERVICE.getAll();
    }

    @GetMapping(value = "/get-categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AnnouncementCategory> getCategories () {
        return ANNOUNCEMENT_CATEGORY_SERVICE.findAll();
    }

    @GetMapping(value = "/getCompanyById/{companyId}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Company> getCompanyById(@PathVariable Long companyId) {
        Optional<Company> optionalCompany = COMPANY_SERVICE.getCompanyById(companyId);
        return optionalCompany.map(company -> new ResponseEntity<>(company, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/get-Employees/{departmentId}")
    public List<UserDTO> getEmployeesByDepartmentId(@PathVariable Long departmentId) {
        List<Employee> employees = EMPLOYEE_SERVICE.getEmployeesByDepartmentId(departmentId);
        return employees.stream()
                .map(employee -> MODEL_MAPPER.map(employee, UserDTO.class))
                .collect(Collectors.toList());
    }


}
