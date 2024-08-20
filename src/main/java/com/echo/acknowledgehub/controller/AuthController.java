package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.EmailDTO;
import com.echo.acknowledgehub.dto.JWTToken;
import com.echo.acknowledgehub.dto.LoginDTO;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.EmailSender;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final EmailSender EMAIL_SENDER;

    @PostMapping("/login")
    private JWTToken login(@RequestBody LoginDTO login) {
        LOGGER.info("Login info : " + login);
        AUTHENTICATION_MANAGER.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        final UserDetails USER_DETAILS = USER_DETAILS_SERVICE.loadUserByUsername(login.getEmail());
        if(login.getPassword().equals("root")){ //For first login.
            return null;
        }else{
            final String JWT_TOKEN = JWT_SERVICE.generateToken(USER_DETAILS.getUsername());
            LOGGER.info("Token : " + JWT_TOKEN);
            return new JWTToken(JWT_TOKEN);
        }
    }

    @GetMapping("/check")
    private CheckingBean check() {
        return this.CHECKING_BEAN;
    }

    @PostMapping("/send-email")
    private CompletableFuture<Void> sendEmail(@RequestBody EmailDTO email){
        LOGGER.info("Email : "+ email);
        return EMAIL_SENDER.sendEmail(email);
    }
}
