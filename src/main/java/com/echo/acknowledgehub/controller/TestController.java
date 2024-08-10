package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.custom_exception.EmailSenderException;
import com.echo.acknowledgehub.custom_exception.XlsxReaderException;
import com.echo.acknowledgehub.dto.EmailDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.util.EmailSender;
import com.echo.acknowledgehub.util.XlsxReader;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/home")
@AllArgsConstructor
public class TestController {

    private static final Logger LOGGER = Logger.getLogger(TestController.class.getName());
    private final EmailSender EMAIL_SENDER;
    private final XlsxReader XLSX_READER;
//
//    @ExceptionHandler(EmailSenderException.class)
//    @PostMapping
//    public CompletableFuture<String> sendEmail(@RequestBody EmailDTO email) {
//        return EMAIL_SENDER.sendEmail(email);
//         //"Send email to "+email.getAddress();
//    }
    @ExceptionHandler(XlsxReaderException.class)
    @PostMapping
    public CompletableFuture<List<Employee>> sendXlsx() {
        try {
           // CompletableFuture<List<Employee>> futureEmployees = XLSX_READER.getEmployees(new FileInputStream("C:/OJT-14/Final Project/excel_import_test.xlsx"));
           return XLSX_READER.getEmployees(new FileInputStream("C:/OJT-14/Final Project/excel_import_test.xlsx"));
        } catch (IOException e) {
            throw new XlsxReaderException("An error occurred when converting the .xlsx to java object.");
        }
    }
}
