package com.echo.acknowledgehub.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EmailSenderException extends RuntimeException{
    public EmailSenderException(String message){
        super(message);
    }
}
