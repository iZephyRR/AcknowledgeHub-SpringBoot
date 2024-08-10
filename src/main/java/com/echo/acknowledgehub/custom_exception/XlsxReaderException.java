package com.echo.acknowledgehub.custom_exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class XlsxReaderException extends RuntimeException{

    public XlsxReaderException(String message){
        super(message);
    }
}
