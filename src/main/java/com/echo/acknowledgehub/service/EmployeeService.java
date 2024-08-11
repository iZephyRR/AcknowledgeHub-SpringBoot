package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final ModelMapper MAPPER;

    @Async
    public CompletableFuture<Optional<Employee>> findById(Long id) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<Optional<Employee>> findByEmail(String email) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findByEmail(email));
    }

    @Async
    public CompletableFuture<Employee> save(UserDTO user) {
        MAPPER.typeMap(UserDTO.class, Employee.class).addMappings(mapper -> {
            mapper.map(UserDTO::getDepartmentId, (Employee e, Long id) -> e.getDepartment().setId(id));
            mapper.map(UserDTO::getCompanyId, (Employee e, Long id) -> e.getCompany().setId(id));
        });
        Employee employee = MAPPER.map(user, Employee.class);
        LOGGER.info("Mapped employee : "+employee);
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(employee));
    }

    @Async
    public CompletableFuture<List<Employee>> saveAll(List<UserDTO> users) {
        List<Employee> employees = new ArrayList<>();
        users.forEach(user-> this.save(user).thenAccept(employees::add));
        return CompletableFuture.completedFuture(employees);
    }

    @Async
    public CompletableFuture<List<Employee>> findAll() {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findAll());
    }

}
