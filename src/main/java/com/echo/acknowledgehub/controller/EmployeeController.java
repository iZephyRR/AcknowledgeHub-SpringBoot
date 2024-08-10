package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.dto.JWTToken;
import com.echo.acknowledgehub.dto.LoginDTO;
import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

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
        final String TOKEN = JWT_SERVICE.generateToken(USER_DETAILS.getUsername());
        LOGGER.info("Token : " + TOKEN);
        return new JWTToken(TOKEN);
    }

    @PostMapping("/ad/add-user")
    private CompletableFuture<Employee> register(@RequestBody UserDTO user) {
        LOGGER.info("Adding user...");
        return EMPLOYEE_SERVICE.save(user);
    }

    @PostMapping("/ad/add-users")
    private CompletableFuture<List<Employee>> register(@RequestBody List<UserDTO> users) {
        LOGGER.info("Adding users...");
        return EMPLOYEE_SERVICE.saveAll(users);
    }
}
