package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.dto.EmployeeProfileDTO;
import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.dto.UsersDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
    private final EmployeeService EMPLOYEE_SERVICE;
    private final JWTService JWT_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final EmployeeRepository employeeRepository;

//     @GetMapping("/mr/users")
//     private List<Employee> findAll() {
//         LOGGER.info("Finding users..");
//         return EMPLOYEE_SERVICE.findAll().join();
//     }

//     @GetMapping("/user/profile")
//     private Optional<Employee> findById() {
//         Long id = CHECKING_BEAN.getId();
//         return EMPLOYEE_SERVICE.findById(id).join();

    
//    @GetMapping("/mr/users")
//    private List<Employee> findAll() {
//        LOGGER.info("Finding users..");
//        return EMPLOYEE_SERVICE.findAll().join();
//    }

    @GetMapping(value = "/mr/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(EMPLOYEE_SERVICE.getAllUsers());
    }

    @GetMapping("/user/profile")
    private EmployeeProfileDTO findById(){
        long id = CHECKING_BEAN.getId();
        return employeeRepository.findByIdForProfile(id);
    }


    @PostMapping("/ad/add-user")
    private Employee register(@RequestBody UserDTO user) {
        return EMPLOYEE_SERVICE.save(user).join();
    }

    @PostMapping("/ad/add-users")
    private CompletableFuture<List<Employee>> register(@RequestBody List<UserDTO> users) {
        LOGGER.info("Adding users...");
        return EMPLOYEE_SERVICE.saveAll(users);
    }

    //Not finish yet!
//    @PostMapping("/ad/add-excel-users")
    private CompletableFuture<List<Employee>> register(@RequestBody UsersDTO users) throws IOException {
        LOGGER.info("Adding users...");
        return EMPLOYEE_SERVICE.saveAll(null);
    }
}
