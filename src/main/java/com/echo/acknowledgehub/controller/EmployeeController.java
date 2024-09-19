package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.base-url}")
@AllArgsConstructor
public class EmployeeController {
    private static final Logger LOGGER = Logger.getLogger(EmployeeController.class.getName());
    private final EmployeeService EMPLOYEE_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final ModelMapper MODEL_MAPPER;

    @GetMapping(value = "/mr/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(EMPLOYEE_SERVICE.getAllUsers());
    }

    @GetMapping("/user/{id}")
    private Optional<Employee> getById(@PathVariable Long id){
        return EMPLOYEE_SERVICE.findById(id).join();
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
        return EMPLOYEE_SERVICE.getProfileInfo(CHECKING_BEAN.getId()).join();
    }

    @GetMapping("/hrs/user/by-department/{id}")
    public List<UserDTO> getEmployeesByDepartmentId(@PathVariable("id") Long id) {
        List<Employee> employees = EMPLOYEE_SERVICE.getEmployeesByDepartmentId(id);
        return employees.stream()
                .map(employee -> MODEL_MAPPER.map(employee, UserDTO.class))
                .collect(Collectors.toList());
    }

    @PostMapping("/user/uploadProfileImage")
    public ResponseEntity<StringResponseDTO> uploadProfileImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            EMPLOYEE_SERVICE.uploadProfileImage(imageFile);
            return ResponseEntity.ok(new StringResponseDTO("Profile image uploaded successfully"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new StringResponseDTO("Failed to upload image"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponseDTO("Employee not found."));
        }
    }

    @GetMapping("/count")
    public long getEmployeeCount() {
        return EMPLOYEE_SERVICE.countEmployees();
    }

    @PostMapping("/ad/main-hr")
    private Employee register(@RequestBody HRDTO mainHRDTO) {
        return EMPLOYEE_SERVICE.saveMainHR(mainHRDTO).join();
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

    @GetMapping("/ad/exists-main-hr")
    private Boolean existsMainHR(){
        return EMPLOYEE_SERVICE.existsMainHR().join();
    }

    @GetMapping(value = "/hrs/employee-count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> countCompany() {
        long count = EMPLOYEE_SERVICE.count();
        return ResponseEntity.ok(count);
    }

}
