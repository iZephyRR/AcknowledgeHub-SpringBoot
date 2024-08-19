package com.echo.acknowledgehub.exception_handler;

public class UserDeactivatedException extends RuntimeException{
    public UserDeactivatedException(String message){
        super(message);
    }
}
