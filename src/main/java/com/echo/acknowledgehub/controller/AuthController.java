package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.JWTToken;
import com.echo.acknowledgehub.dto.LoginDTO;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}/auth")
@AllArgsConstructor
public class AuthController {
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());
    private final AuthenticationManager AUTHENTICATION_MANAGER;
    private final UserDetailsService USER_DETAILS_SERVICE;
    private final JWTService JWT_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final EmployeeService EMPLOYEE_SERVICE;

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

    @GetMapping("/check")
    private CheckingBean check() {
        return this.CHECKING_BEAN;
    }
    @GetMapping(value = "/checking", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CheckingBean> streamSingleObject() {
        return Flux.interval(Duration.ofSeconds(5))
                .map(sequence -> {
                    LOGGER.info("Emitting CheckingBean: " + CHECKING_BEAN);
                    return CHECKING_BEAN;
                });
    }

    @GetMapping("/is-first")
    private CompletableFuture<Boolean> isFirstLogin(@RequestBody String email) {
        return EMPLOYEE_SERVICE.isFirstTime(email);
    }
}
