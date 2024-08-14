package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class DepartmentService {
    private static final Logger LOGGER = Logger.getLogger(DepartmentService.class.getName());
    private final DepartmentRepository DEPARTMENT_REPOSITORY;

    @Async
    public CompletableFuture<Optional<Department>> findById(Long id){
        return CompletableFuture.completedFuture(DEPARTMENT_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<Optional<Department>> findByName(String name){
        return CompletableFuture.completedFuture(DEPARTMENT_REPOSITORY.findByName(name));
    }

    @Async
    public CompletableFuture<Department> save(Department department){
        return CompletableFuture.completedFuture(DEPARTMENT_REPOSITORY.save(department));
    }
}
