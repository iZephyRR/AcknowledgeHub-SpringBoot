package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(MAPPER.map(user, Employee.class)));
    }

    @Async
    public CompletableFuture<List<Employee>> saveAll(List<UserDTO> users) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.saveAll(users.stream().map(source -> MAPPER.map(source, Employee.class)).collect(Collectors.toList())));
    }

    @Async
    public CompletableFuture<List<Employee>> findAll() {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findAll());
    }

}
