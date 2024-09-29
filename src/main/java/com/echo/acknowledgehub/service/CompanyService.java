package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.SystemDataBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.dto.DepartmentDTO;
import com.echo.acknowledgehub.dto.HRDTO;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.repository.CompanyRepository;
import com.echo.acknowledgehub.repository.DepartmentRepository;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CompanyService {
    private static final Logger LOGGER = Logger.getLogger(CompanyService.class.getName());
    private final CompanyRepository COMPANY_REPOSITORY;
    private final PasswordEncoder PASSWORD_ENCODER;
    private final SystemDataBean SYSTEM_DATA_BEAN;
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final DepartmentRepository DEPARTMENT_REPOSITORY;

    @Async
    public CompletableFuture<CompanyDTO> findById(Long id) {
        Optional<Company> optionalCompany=COMPANY_REPOSITORY.findById(id);
        if(optionalCompany.isPresent()){
            return CompletableFuture.completedFuture(mapToDTO(optionalCompany.get()));
        }else{
            throw new DataNotFoundException("Cannot find company");
        }
    }

    @Async
    public CompletableFuture<CompanyDTO> findDTOById(Long id) {
        CompanyDTO companyDTO = COMPANY_REPOSITORY.findDTOByID(id);
        LOGGER.info("companyDTO " + companyDTO);
        if (companyDTO != null) {
            return CompletableFuture.completedFuture(companyDTO);
        } else {
            throw new DataNotFoundException("Cannot find company.");
        }
    }

    @Async
    public CompletableFuture<Company> save(Company company) {
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.save(company));
    }

    @Async
    public CompletableFuture<HRDTO> saveHR(HRDTO hrdto) {
        Employee employee = new Employee();
        employee.setName(hrdto.getHrName());
        employee.setEmail(hrdto.getHrEmail());
        employee.setStaffId(hrdto.getStaffId());
        employee.setCompany(this.save(new Company(hrdto.getCompanyName())).join());
        employee.setPassword(PASSWORD_ENCODER.encode(SYSTEM_DATA_BEAN.getDefaultPassword()));
        employee.setRole(EmployeeRole.HR);
        employee.setTelegramUsername(hrdto.getTelegramUsername());
        EMPLOYEE_REPOSITORY.save(employee);
        return CompletableFuture.completedFuture(hrdto);
    }

    @Async
    public CompletableFuture<Optional<Company>> findByName(String name) {
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.findByName(name));
    }

//    public List<CompanyDTO> getAll() {
//        List<Company> companies = COMPANY_REPOSITORY.findAll();
//        List<CompanyDTO> companyDTOS = new LinkedList<>();
//        companies.forEach((company -> {
//            CompanyDTO companyDTO = new CompanyDTO();
//            companyDTO.setId(company.getId());
//            companyDTO.setName(company.getName());
//            List<DepartmentDTO> departmentDTOS = new LinkedList<>();
//            List<Department> departments = DEPARTMENT_REPOSITORY.findByCompanyId(company.getId());
//            departments.forEach((department -> {
//                DepartmentDTO departmentDTO = new DepartmentDTO();
//                departmentDTO.setId(department.getId());
//                departmentDTO.setName(department.getName());
//                List<Employee>employees=EMPLOYEE_REPOSITORY.getByDepartmentId(department.getId());
//                departmentDTO.setEmployees(employees);
//                departmentDTOS.add(departmentDTO);
//            }));
//            companyDTO.setDepartments(departmentDTOS);
//            companyDTOS.add(companyDTO);
//        }));
//        return companyDTOS;
//    }

    public List<CompanyDTO> getAll() {
        return mapToDTOList(COMPANY_REPOSITORY.findAll());
    }

    public List<CompanyDTO> getAllDTO() {
        return COMPANY_REPOSITORY.findAllDTO();
    }

    public Optional<Company> getCompanyById(Long id) {
        return COMPANY_REPOSITORY.findById(id);
    }

    public boolean existsById(Long sendTo) {
        return COMPANY_REPOSITORY.existsById(sendTo);
    }

    public long countCompany() {
        return COMPANY_REPOSITORY.count();
    }

    @Transactional
    public String getCompanyName(Long companyId) {
        return COMPANY_REPOSITORY.findCompanyNameById(companyId);
    }

    @Async
    public CompletableFuture<Company> getByDepartmentId(Long departmentId) {
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.getByDepartmentId(departmentId));
    }

    public Company findByEmployeeId(Long employeeId) {
        return COMPANY_REPOSITORY.findByEmployeeId(employeeId);
    }

    private CompanyDTO mapToDTO(Company company){
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(company.getId());
        companyDTO.setName(company.getName());
        List<DepartmentDTO> departmentDTOS = new LinkedList<>();
        List<Department> departments = DEPARTMENT_REPOSITORY.findByCompanyId(company.getId());
        departments.forEach((department -> {
            DepartmentDTO departmentDTO = new DepartmentDTO();
            departmentDTO.setId(department.getId());
            departmentDTO.setName(department.getName());
            List<Employee>employees=EMPLOYEE_REPOSITORY.getByDepartmentId(department.getId());
            departmentDTO.setEmployees(employees);
            departmentDTOS.add(departmentDTO);
        }));
        companyDTO.setDepartments(departmentDTOS);
        return companyDTO;
    }

    private List<CompanyDTO> mapToDTOList(List<Company> companies){
        List<CompanyDTO> companyDTOS = new LinkedList<>();
        companies.forEach((company -> {
            companyDTOS.add(mapToDTO(company));
        }));
        return companyDTOS;
    }

//    public Company getCompanyById(Long id) {
//        return COMPANY_REPOSITORY.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
//    }



}
