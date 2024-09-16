package com.echo.acknowledgehub.exception_handler;

public class RestingSystemException extends RuntimeException{
    public RestingSystemException(){
        super("System is resting now.");
    }
}
