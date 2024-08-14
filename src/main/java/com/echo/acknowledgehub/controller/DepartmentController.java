package com.echo.acknowledgehub.controller;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/department")
@AllArgsConstructor
public class DepartmentController {
    private static final Logger LOGGER = Logger.getLogger(DepartmentController.class.getName());
    private final DepartmentService DEPARTMENT_SERVICE;
}
