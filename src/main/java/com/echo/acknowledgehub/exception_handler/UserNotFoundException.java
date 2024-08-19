package com.echo.acknowledgehub.exception_handler;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("Cannot get user.");
    }
}
