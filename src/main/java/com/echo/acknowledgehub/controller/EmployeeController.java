package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.service.FirebaseNotificationService;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
    private final EmployeeService EMPLOYEE_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper MODEL_MAPPER;
    private final FirebaseNotificationService FIREBASE_NOTIFICATION_SERVICE;


    @GetMapping(value = "/mr/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(EMPLOYEE_SERVICE.getAllUsers());
    }


    @GetMapping("/mr/find-all")
    private List<Employee> findAll(){
        return EMPLOYEE_SERVICE.getAll().join();
    }

    @GetMapping(value = "/getUsersByCompanyId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getUserByCompanyId () {
        return ResponseEntity.ok(EMPLOYEE_SERVICE.getUsersByCompanyId(CHECKING_BEAN.getCompanyId()));
    }


    @GetMapping("/user/profile")
    private EmployeeProfileDTO findById(){
        long id = CHECKING_BEAN.getId();
        return employeeRepository.findByIdForProfile(id);
    }


    @GetMapping("/hrs/user/by-department/{id}")
    public List<UserDTO> getEmployeesByDepartmentId(@PathVariable("id") Long id) {
        List<Employee> employees = EMPLOYEE_SERVICE.getEmployeesByDepartmentId(id);
        return employees.stream()
                .map(employee -> MODEL_MAPPER.map(employee, UserDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/auth/find-name-by-email")
    private StringResponseDTO findNameByEmail(@RequestBody String email) {
        return EMPLOYEE_SERVICE.findNameByEmail(email).join();
    }


    @GetMapping("/count")
    public long getEmployeeCount() {
        return EMPLOYEE_SERVICE.countEmployees();
    }

    @PostMapping("/ad/add-user")
    private Employee register(@RequestBody UserDTO user) {
        return EMPLOYEE_SERVICE.save(user).join();
    }


    @PostMapping("/hrs/add-users")
    private List<Employee> register(@RequestBody UserExcelDTO users) {
        LOGGER.info("Adding users..."+users);
        return EMPLOYEE_SERVICE.saveAll(users).join();
    }
    @PutMapping("/hrs/update-users")
    private List<Employee> update(@RequestBody List<UserDTO> users){
        LOGGER.info("Updating users : "+users);
        return EMPLOYEE_SERVICE.updateUsers(users).join();
    }

    @GetMapping("/hrs/get-uniques")
    private UniqueFieldsDTO getUniqueFields(){
        return EMPLOYEE_SERVICE.getUniques().join();


//    @GetMapping(value = "/getEmployeesWho1DNoted/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<EmployeeNotedDTO>> getEmployeesWho1DNoted(@PathVariable("id") String announcementId) {
//        LOGGER.info("in one day");
//        List<Long> userIdList = FIREBASE_NOTIFICATION_SERVICE.getNotificationsAndMatchWithEmployees(Long.parseLong(announcementId),1);
//        return ResponseEntity.ok(EMPLOYEE_SERVICE.getEmployeeWhoNoted(userIdList));
//    }
//
//    @GetMapping(value = "/getEmployeesWho3DNoted/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<EmployeeNotedDTO>> getEmployeesWho3DNoted(@PathVariable("id") String announcementId) {
//        LOGGER.info("in three day");
//        List<Long> userIdList = FIREBASE_NOTIFICATION_SERVICE.getNotificationsAndMatchWithEmployees(Long.parseLong(announcementId),3);
//        return ResponseEntity.ok(EMPLOYEE_SERVICE.getEmployeeWhoNoted(userIdList));

    }

}
