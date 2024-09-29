package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.DepartmentDTO;
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
@RequestMapping("${app.api.base-url}/hrs/department")
@AllArgsConstructor
public class DepartmentController {
    private static final Logger LOGGER = Logger.getLogger(DepartmentController.class.getName());
    private final DepartmentService DEPARTMENT_SERVICE;
    private final CheckingBean CHECKING_BEAN;

    @PutMapping
    private Department save(@RequestBody Department department){
        LOGGER.info(department.toString());
        return DEPARTMENT_SERVICE.save(department).join();
    }

    @GetMapping("/by-company/{id}")
    private List<DepartmentDTO> findAllDTOByCompanyId(@PathVariable("id")Long id){
        return DEPARTMENT_SERVICE.getAllDTOByCompany(id);
    }

    @GetMapping("/dto/{id}")
    private DepartmentDTO findById(@PathVariable("id") Long id){
        return DEPARTMENT_SERVICE.findDTOById(id).join();
    }

    @DeleteMapping("/{id}")
    private void delete(@PathVariable("id") Long id){
        DEPARTMENT_SERVICE.delete(id);
    }



}
