package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.exception_handler.EmailSenderException;
import com.echo.acknowledgehub.exception_handler.XlsxReaderException;
import com.echo.acknowledgehub.util.JWTService;
import com.echo.acknowledgehub.util.BaseURL;
import com.echo.acknowledgehub.util.XlsxReader;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Logger;

@RestController
@AllArgsConstructor
@RequestMapping("${app.api.base-url}")
public class TestController {

    private static final Logger LOGGER = Logger.getLogger(TestController.class.getName());
    private final XlsxReader XLSX_READER;
    private final BaseURL BASE_URL;
    private final JWTService JWT_SERVICE;

//    @PostMapping("/test")
//    public CompletableFuture<List<Employee>> sendXlsx() {
//        LOGGER.info("Base URl "+BASE_URL);
//        try {
//           // CompletableFuture<List<Employee>> futureEmployees = XLSX_READER.getEmployees(new FileInputStream("C:/OJT-14/Final Project/excel_import_test.xlsx"));
//           return XLSX_READER.getEmployees(new FileInputStream("C:/OJT-14/Final Project/excel_import_test.xlsx"));
//        } catch (IOException e) {
//            throw new XlsxReaderException();
//        }
//    }
    @PostMapping("/test")
    public void test() {
        throw new XlsxReaderException();
    }
}
