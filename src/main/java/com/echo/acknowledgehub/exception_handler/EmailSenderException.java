package com.echo.acknowledgehub.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class EmailSenderException extends RuntimeException{
    public EmailSenderException(String message){
        super(message);
    }
}
