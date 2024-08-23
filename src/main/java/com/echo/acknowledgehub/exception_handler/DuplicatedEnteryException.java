package com.echo.acknowledgehub.exception_handler;

public class DuplicatedEnteryException extends RuntimeException{
    public DuplicatedEnteryException(){
        super("This category is already exist.");
    }
}
