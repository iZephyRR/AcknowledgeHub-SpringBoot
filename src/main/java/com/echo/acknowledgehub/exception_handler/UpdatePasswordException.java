package com.echo.acknowledgehub.exception_handler;

public class UpdatePasswordException extends RuntimeException{
    public UpdatePasswordException(){
        super("Failed to update password.");
    }
}
