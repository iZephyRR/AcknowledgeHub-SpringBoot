package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.dto.UsersDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
    private final EmployeeService EMPLOYEE_SERVICE;

    @GetMapping("/ad/users")
    private CompletableFuture<List<Employee>> findAll(){
        LOGGER.info("Finding users..");
        return EMPLOYEE_SERVICE.findAll();
    }

    @PostMapping("/ad/add-user")
    private CompletableFuture<Employee> register(@RequestBody UserDTO user) {
        LOGGER.info("Adding a user...");
        return EMPLOYEE_SERVICE.save(user);
    }

    @PostMapping("/ad/add-users")
    private CompletableFuture<List<Employee>> register(@RequestBody List<UserDTO> users) {
        LOGGER.info("Adding users...");
        return EMPLOYEE_SERVICE.saveAll(users);
    }
//Not finish yet!
//    @PostMapping("/ad/add-excel-users")
    private CompletableFuture<List<Employee>> register(@RequestBody UsersDTO users) throws IOException {
        LOGGER.info("Adding users...");
        return EMPLOYEE_SERVICE.saveAll(null);
    }
}
