package com.echo.acknowledgehub.exception_handler;

public class XlsxReaderException extends RuntimeException{

    public XlsxReaderException(){
        super("An error occurred when converting the .xlsx to an object.");
    }
}
