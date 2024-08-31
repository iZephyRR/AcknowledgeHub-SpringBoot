package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}/mhr/department")
@AllArgsConstructor
public class DepartmentController {
    private static final Logger LOGGER = Logger.getLogger(DepartmentController.class.getName());
    private final DepartmentService DEPARTMENT_SERVICE;

    @PostMapping
    private Department save(Department department){
        return DEPARTMENT_SERVICE.save(department).join();
    }
    @GetMapping
    private List<Department> findAll(){
        return DEPARTMENT_SERVICE.getAll();
    }
    @GetMapping("/{id}")
    private Optional<Department> findById(@PathVariable("id") Long id){
        return DEPARTMENT_SERVICE.findById(id).join();
    }
    @DeleteMapping("/{id}")
    private void delete(@PathVariable("id") Long id){
        DEPARTMENT_SERVICE.delete(id);
    }
}
