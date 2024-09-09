package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.bean.SystemDataBean;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.EmailSender;
import com.echo.acknowledgehub.util.JWTService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class AuthController {
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());
    private final AuthenticationManager AUTHENTICATION_MANAGER;
    private final UserDetailsService USER_DETAILS_SERVICE;
    private final JWTService JWT_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final EmailSender EMAIL_SENDER;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final SystemDataBean SYSTEM_DATA_BEAN;

    @PostMapping("/auth/login")
    private StringResponseDTO login(@RequestBody LoginDTO login) {
        LOGGER.info("Login info : " + login);
        AUTHENTICATION_MANAGER.authenticate(new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword()));
        final UserDetails USER_DETAILS = USER_DETAILS_SERVICE.loadUserByUsername(login.getEmail());
        if (login.getPassword().equals(SYSTEM_DATA_BEAN.getDefaultPassword())) { //For first login.
            // return new StringResponseDTO("NAME_".concat(CHECKING_BEAN.getName()).concat("_ID_").concat(CHECKING_BEAN.getId().toString()));
            return new StringResponseDTO("NAME_".concat(CHECKING_BEAN.getName()));
        } else {
            final String JWT_TOKEN = JWT_SERVICE.generateToken(USER_DETAILS.getUsername());
            LOGGER.info("Token : " + JWT_TOKEN);
            return new StringResponseDTO(JWT_TOKEN);
        }
    }

    @PutMapping("/auth/change-password")
    private ResponseEntity<Void> updatePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        return ResponseEntity.ok(EMPLOYEE_SERVICE.updatePassword(changePasswordDTO).join());
    }

    @GetMapping("/auth/check")
    private CheckingBean check() {
        return this.CHECKING_BEAN;
    }

    @PostMapping(value = "/auth/send-email", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private CompletableFuture<Void> sendEmail(@ModelAttribute EmailDTO email) throws IOException {
        LOGGER.info("Email : " + email);
        return EMAIL_SENDER.sendEmail(email);
    }

    @GetMapping("/auth/check-email")
    private ResponseEntity<Boolean> checkEmail(@RequestBody String email) {
        return ResponseEntity.ok(EMPLOYEE_SERVICE.checkEmail(email).join());
    }

    @PostMapping("/auth/is-password-default")
    private BooleanResponseDTO isPasswordDefault(@RequestBody String email){
        return new BooleanResponseDTO(EMPLOYEE_SERVICE.isPasswordDefault(email).join());
    }

    @GetMapping("ad/default-password")
    private StringResponseDTO getDefaultPassword(){
        return new StringResponseDTO(SYSTEM_DATA_BEAN.getDefaultPassword());
    }
    @PutMapping("/ad/default-password")
    private StringResponseDTO changeDefaultPassword(@RequestBody String password){
        EMPLOYEE_SERVICE.changeDefaultPassword(password).join();
        return new StringResponseDTO(SYSTEM_DATA_BEAN.getDefaultPassword());
    }
    @PutMapping("ad/make-password-as-default")
    private BooleanResponseDTO makePasswordAsDefault(@RequestBody Long id){
        return new BooleanResponseDTO(EMPLOYEE_SERVICE.makePasswordAsDefault(id).join()>0);
    }

    @GetMapping("/auth/sever-connection-test")
    private StringResponseDTO severConnectionTest(){
        return new StringResponseDTO("Test success");
    }
}
