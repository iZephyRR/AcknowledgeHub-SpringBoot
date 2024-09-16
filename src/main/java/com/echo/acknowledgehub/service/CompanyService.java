package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.SystemDataBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.dto.HRDTO;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.repository.CompanyRepository;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder PASSWORD_ENCODER;
    private final SystemDataBean SYSTEM_DATA_BEAN;
    private final EmployeeRepository EMPLOYEE_REPOSITORY;

    @Async
    public CompletableFuture<Optional<Company>> findById(Long id){
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<CompanyDTO> findDTOById(Long id){
        CompanyDTO companyDTO=COMPANY_REPOSITORY.findDTOByID(id);
        LOGGER.info("companyDTO "+companyDTO);
        if(companyDTO!=null){
            return CompletableFuture.completedFuture(companyDTO);
        }else{
            throw new DataNotFoundException("Cannot find company.");
        }
    }

    @Async
    public CompletableFuture<Company> save(Company company){
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.save(company));
    }

    @Async
    public CompletableFuture<HRDTO> saveHR(HRDTO hrdto){
        Employee employee = new Employee();
        employee.setName(hrdto.getHrName());
        employee.setEmail(hrdto.getHrEmail());
        employee.setStaffId(hrdto.getStaffId());
        employee.setCompany(this.save(new Company(hrdto.getCompanyName())).join());
        employee.setPassword(PASSWORD_ENCODER.encode(SYSTEM_DATA_BEAN.getDefaultPassword()));
        employee.setRole(EmployeeRole.MAIN_HR);
        EMPLOYEE_REPOSITORY.save(employee);
        return CompletableFuture.completedFuture(hrdto);
    }

    @Async
    public CompletableFuture<Optional<Company>> findByName(String name){
        return CompletableFuture.completedFuture(COMPANY_REPOSITORY.findByName(name));
    }

    public List<Company> getAll() {
        return COMPANY_REPOSITORY.findAll();
    }

    public List<CompanyDTO> getAllDTO(){
        return COMPANY_REPOSITORY.findAllDTO();
    }

    public Optional<Company> getCompanyById(Long id){
        return COMPANY_REPOSITORY.findById(id);
    }

    public boolean existsById(Long sendTo) {
        return COMPANY_REPOSITORY.existsById(sendTo);
    }

    @Transactional
    public String getCompanyName(Long companyId) {
        return COMPANY_REPOSITORY.findCompanyNameById(companyId);
    }
}
