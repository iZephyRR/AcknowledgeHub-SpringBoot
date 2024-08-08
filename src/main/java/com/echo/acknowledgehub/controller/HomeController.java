package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.CustomException.EmailSenderException;
import com.echo.acknowledgehub.dto.EmailDTO;
import com.echo.acknowledgehub.util.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/home")
@AllArgsConstructor
public class HomeController {
    private static final Logger LOGGER = Logger.getLogger(HomeController.class.getName());

    private final EmailSender EMAIL_SENDER;

    @PostMapping
    public ResponseEntity<String> sendEmail(@RequestBody EmailDTO email){
        try {
            EMAIL_SENDER.sendEmail(email);
            return ResponseEntity.ok("Send an email to "+email.getAddress());
        }catch (EmailSenderException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }
}
