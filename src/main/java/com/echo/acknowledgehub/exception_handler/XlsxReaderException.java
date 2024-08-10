package com.echo.acknowledgehub.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class XlsxReaderException extends RuntimeException{

    public XlsxReaderException(){
        super("An error occurred when converting the .xlsx to an object.");
    }
}
