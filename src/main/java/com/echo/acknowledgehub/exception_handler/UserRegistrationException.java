package com.echo.acknowledgehub.exception_handler;

public class UserRegistrationException extends RuntimeException{

    public UserRegistrationException(){
        super("An unexpected error occurred when register user. Please try again later.");
    }
}