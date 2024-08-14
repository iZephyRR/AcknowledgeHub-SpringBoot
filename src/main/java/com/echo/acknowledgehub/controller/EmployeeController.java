package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.dto.JWTToken;
import com.echo.acknowledgehub.dto.LoginDTO;
import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.dto.UsersDTO;
import com.echo.acknowledgehub.persistence.entity.Employee;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final AuthenticationManager AUTHENTICATION_MANAGER;
    private final UserDetailsService USER_DETAILS_SERVICE;
    private final JWTService JWT_SERVICE;

    @PostMapping("/login")
    private JWTToken login(@RequestBody LoginDTO login) {
        LOGGER.info("Login info : " + login);
        AUTHENTICATION_MANAGER.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        final UserDetails USER_DETAILS = USER_DETAILS_SERVICE.loadUserByUsername(login.getEmail());
        LOGGER.info("username " + USER_DETAILS.getUsername());
        final String JWT_TOKEN = JWT_SERVICE.generateToken(USER_DETAILS.getUsername());
        LOGGER.info("Token : " + JWT_TOKEN);
        return new JWTToken(JWT_TOKEN);
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

    @PostMapping("/ad/add-excel-users")
    private CompletableFuture<List<Employee>> register(@RequestBody UsersDTO users) throws IOException {
        LOGGER.info("Adding users...");
        return EMPLOYEE_SERVICE.saveAll(null);
    }
}
