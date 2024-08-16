package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Logger;

@RestController
@AllArgsConstructor
@RequestMapping("${app.api.base-url}")
public class TestController {

    private static final Logger LOGGER = Logger.getLogger(TestController.class.getName());
    private final JWTService JWT_SERVICE;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final CheckingBean CHECKING_BEAN;

//@GetMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    LOGGER.info("Starting request...");//public Flux<CheckingBean> streamEvents(@RequestHeader("Authorization") String token) {
//    Long id = Long.parseLong(JWT_SERVICE.extractId(token.substring(7)));
//    return Mono.fromFuture(EMPLOYEE_SERVICE.findById(id))
//            .flatMapMany(data -> {
//                return data.map(employee -> Flux.interval(Duration.ofSeconds(5))
//                        .map(sequence -> new CheckingBean(employee.getStatus(), employee.getRole()))).orElseGet(Flux::empty);
//            });
//}
//@GetMapping(value = "/test", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//public Flux<CheckingBean> streamEvents(@RequestHeader("Authorization") String token) {
//    LOGGER.info("Starting request...");
//    Long id = Long.parseLong(JWT_SERVICE.extractId(token.substring(7)));
//
//    return Flux.interval(Duration.ofSeconds(5))
//            .flatMap(sequence -> Mono.fromFuture(EMPLOYEE_SERVICE.findById(id))
//                    .flatMapMany(data -> data.map(employee ->
//                                    Flux.just(new CheckingBean(employee.getStatus(), employee.getRole())))
//                            .orElseGet(Flux::empty)
//                    )
//            );
//}
//@GetMapping("test")
//    private void test(){
//    LOGGER.info("Checking : "+CHECKING_BEAN);
//}

}
