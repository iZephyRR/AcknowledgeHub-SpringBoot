package com.echo.acknowledgehub.custom_exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailSenderException extends RuntimeException{
    public EmailSenderException(String message){
        super(message);
    }
}
