package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.bean.SystemDataBean;
import com.echo.acknowledgehub.bean.TempDataBean;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.service.AnnouncementService;
import com.echo.acknowledgehub.service.CompanyService;
//import com.echo.acknowledgehub.service.TelegramService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@AllArgsConstructor
@RequestMapping("${app.api.base-url}")
public class TestController {

    private static final Logger LOGGER = Logger.getLogger(TestController.class.getName());
    private final CheckingBean CHECKING_BEAN;
    private final CompanyService COMPANY_SERVICE;
    private final SystemDataBean SYSTEM_DATA_BEAN;
    //private final TelegramService TELEGRAM_SERVICE;
    private final AnnouncementService ANNOUNCEMENT_SERVICE;
    private final TempDataBean TEMP_DATA_BEAN;

    @GetMapping("/auth/test")
    public ResponseEntity<Void> downloadFile() throws IOException {

        try {
            Thread.sleep(80 * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/test")
    private String test() {
        return "testComplete";
    }

    @GetMapping("/hrmh/test")
    private String hrmhTest() {
        return "testComplete";
    }


//    @GetMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<CheckingBean> streamSingleObject() {
//        return Flux.interval(Duration.ofSeconds(5))
//                .map(sequence -> {
//                    LOGGER.info("Emitting CheckingBean: " + CHECKING_BEAN);
//                    return CHECKING_BEAN;
//                });
//    }

//    @GetMapping(value = "/test1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> streamEvents() {
//        return Flux.interval(Duration.ofSeconds(5))
//                .map(sequence -> {
//                    LOGGER.info("Emitting CheckingBean: " + CHECKING_BEAN);
//                    return CHECKING_BEAN;
//                });
//    }


    @GetMapping("/hr/test")
    private String hrTest() {
        return "testComplete";
    }

    @GetMapping("/sf/test")
    private String sfTest() {
        return "testComplete";
    }

    @GetMapping("/ad/test")
    private String adTest() {
        return "testComplete";
    }

    @GetMapping("/bd/test")
    private String bdTest() {
        return "testComplete";
    }

    @GetMapping("/test-get-companies")
    public List<Company> getCompanies() {
        return COMPANY_SERVICE.getAll();
    }
}

