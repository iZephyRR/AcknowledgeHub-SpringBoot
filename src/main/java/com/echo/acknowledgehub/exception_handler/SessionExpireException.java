package com.echo.acknowledgehub.exception_handler;

public class SessionExpireException extends RuntimeException{
    public SessionExpireException(){
        super("Session expired.");
    }
}
