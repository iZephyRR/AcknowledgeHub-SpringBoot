
//package com.echo.acknowledgehub.controller;
//import com.echo.acknowledgehub.bean.CheckingBean;
//import com.echo.acknowledgehub.entity.Company;
//import com.echo.acknowledgehub.service.CompanyService;
//import com.echo.acknowledgehub.service.EmployeeService;
//import com.echo.acknowledgehub.service.TelegramService;
//import com.echo.acknowledgehub.util.JWTService;
//import lombok.AllArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.logging.Logger;
//
//@RestController
//@AllArgsConstructor
//@RequestMapping("${app.api.base-url}")
//public class TestController {
//
//    private static final Logger LOGGER = Logger.getLogger(TestController.class.getName());
//    private final JWTService JWT_SERVICE;
//    private final EmployeeService EMPLOYEE_SERVICE;
//    private final CheckingBean CHECKING_BEAN;
//    private final CompanyService COMPANY_SERVICE;
//    private final TelegramService TELEGRAM_SERVICE;
//
//    @GetMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<CheckingBean> streamSingleObject() {
//        return Flux.interval(Duration.ofSeconds(5))
//                .map(sequence -> {
//                    LOGGER.info("Emitting CheckingBean: " + CHECKING_BEAN);
//                    return CHECKING_BEAN;
//                });
//    }

////    @GetMapping(value = "/test1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
////    public Flux<String> streamEvents() {
////        return Flux.interval(Duration.ofSeconds(5))
////                .map(sequence -> "Server event at " + LocalTime.now());
////    }
//    @GetMapping("/user/test")
//    private String test(){
//        return "testComplete";
//    }
//
//    @GetMapping("/hrmh/test")
//    private String hrmhTest(){
//        return "testComplete";
//    }
//
//    @GetMapping("/hr/test")
//    private String hrTest(){
//        return "testComplete";
//    }
//
//    @GetMapping("/sf/test")
//    private String sfTest(){
//        return "testComplete";
//    }
//
//    @GetMapping("/ad/test")
//    private String adTest(){
//        return "testComplete";
//    }
//
//    @GetMapping("/bd/test")
//    private String bdTest(){
//        return "testComplete";
//    }
//
//    @GetMapping("/test-get-companies")
//    public List<Company> getCompanies () {
//        return COMPANY_SERVICE.getAllCompanies();
//    }
//
//    @GetMapping("/mr/send-message")
//    public void sendMessage(@RequestBody String text) throws TelegramApiException {
//        TELEGRAM_SERVICE.sendMessage(1655222570L,text);
//    }
//
////    @GetMapping("/test/sendMessageForNotice")
////    public void sendMessageForNotice(@RequestBody Long chatId) throws TelegramApiException {
////        TELEGRAM_SERVICE.sendMessageForNotice(chatId);
////    }
//
////@GetMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
////public Flux<CheckingBean> streamEvents(@RequestHeader("Authorization") String token) {
////    LOGGER.info("Starting request...");
////    Long id = Long.parseLong(JWT_SERVICE.extractId(token.substring(7)));
////
////    return Flux.interval(Duration.ofSeconds(5))
////            .flatMap(sequence -> Mono.fromFuture(EMPLOYEE_SERVICE.findById(id))
////                    .flatMapMany(data -> data.map(employee ->
////                                    Flux.just(new CheckingBean(employee.getStatus(), employee.getRole())))
////                            .orElseGet(Flux::empty)
////                    )
////            );
////}
////@GetMapping("test")
////    private void test(){
////    LOGGER.info("Checking : "+CHECKING_BEAN);
////}
//
//}
