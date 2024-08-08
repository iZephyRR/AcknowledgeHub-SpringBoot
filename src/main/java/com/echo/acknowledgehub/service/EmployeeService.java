package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service

public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository=employeeRepository;

    }
}
