package com.echo.acknowledgehub.exception_handler;

public class EmailSenderException extends RuntimeException{
    public EmailSenderException(String message){
        super(message);
    }
}
