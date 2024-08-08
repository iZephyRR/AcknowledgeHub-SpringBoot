package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

@Service

public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private DepartmentService(DepartmentRepository departmentRepository){
        this.departmentRepository=departmentRepository;

    }
}
