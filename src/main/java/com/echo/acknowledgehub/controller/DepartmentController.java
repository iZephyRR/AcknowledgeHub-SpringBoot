package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}/mr/")
@AllArgsConstructor
public class DepartmentController {
    private static final Logger LOGGER = Logger.getLogger(DepartmentController.class.getName());
    private final DepartmentService DEPARTMENT_SERVICE;


}
