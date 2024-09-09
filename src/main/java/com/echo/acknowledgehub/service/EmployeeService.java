package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.*;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.BooleanResponseDTO;
import com.echo.acknowledgehub.dto.ChangePasswordDTO;
import com.echo.acknowledgehub.dto.StringResponseDTO;
import com.echo.acknowledgehub.dto.UserDTO;

import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.exception_handler.UpdatePasswordException;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final ModelMapper MAPPER;
    private final PasswordEncoder PASSWORD_ENCODER;

    @Async
    public CompletableFuture<Optional<Employee>> findById(Long id) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<Optional<Employee>> findByEmail(String email) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findByEmail(email));
    }

    @Async
    public CompletableFuture<List<Long>> findByDepartmentId(Long departmentId) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findByDepartmentId(departmentId));
    }

    @Async
    public CompletableFuture<List<Long>> findByCompanyId(Long companyId) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findByCompanyId(companyId));
    }

    @Async
    public CompletableFuture<Boolean> checkEmail(String email) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.existsByEmail(email));
    }

    @Async
    public CompletableFuture<BooleanResponseDTO> isPasswordDefault(String email) {
        return CompletableFuture.completedFuture(new BooleanResponseDTO(PASSWORD_ENCODER.matches("root", EMPLOYEE_REPOSITORY.findPasswordByEmail(email))));
    }

    @Async
    public CompletableFuture<StringResponseDTO> findNameByEmail(String email) {
        String name = EMPLOYEE_REPOSITORY.findNameByEmail(email);
        if (name != null) {
            return CompletableFuture.completedFuture(new StringResponseDTO(name));
        } else {
            throw new DataNotFoundException("Cannot find name.");
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Void> updatePassword(ChangePasswordDTO changePasswordDTO) {
        LOGGER.info("Requested change password : " + changePasswordDTO);
        int updatedRows = EMPLOYEE_REPOSITORY.updatePassword(changePasswordDTO.getEmail(), PASSWORD_ENCODER.encode(changePasswordDTO.getPassword()));
        LOGGER.info("Updated rows : " + updatedRows);
        if (updatedRows > 0) {
            return CompletableFuture.completedFuture(null);
        } else {
            throw new UpdatePasswordException("Failed to update password.");
        }
    }

    @Async
    public CompletableFuture<Employee> save(UserDTO user) {
        MAPPER.typeMap(UserDTO.class, Employee.class).addMappings(mapper -> {
            mapper.map(UserDTO::getDepartmentId, (Employee e, Long id) -> e.getDepartment().setId(id));
            mapper.map(UserDTO::getCompanyId, (Employee e, Long id) -> e.getCompany().setId(id));
        });
        Employee employee = MAPPER.map(user, Employee.class);
        employee.setPassword(PASSWORD_ENCODER.encode("root"));
        LOGGER.info("Mapped employee : " + employee);
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(employee));
    }

    //Not finish yet!
    @Async
    public CompletableFuture<List<Employee>> saveAll(List<UserDTO> users) {
        List<Employee> employees = new ArrayList<>();
        users.forEach(user -> this.save(user).thenAccept(employees::add));
        return CompletableFuture.completedFuture(employees);
    }

    public Long getEmployeeIdByTelegramUsername(String telegramUsername) {
        return EMPLOYEE_REPOSITORY.getEmployeeIdByTelegramUsername(telegramUsername);
    }

    public EmployeeProfileDTO findByIdForProfile(long id) {
        return EMPLOYEE_REPOSITORY.findByIdForProfile(id);
    }
    public List<Long> getMainHRAndHRIds() {
        List<EmployeeRole> roles = Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.HR);
        return EMPLOYEE_REPOSITORY.findAllByRole(roles)
                .stream()
                .map(Employee::getId)
                .collect(Collectors.toList());
    }
    @Transactional
    public Employee findByTelegramUsername(String username) {
        return EMPLOYEE_REPOSITORY.findByTelegramUsername(username);
    }

    @Transactional
    public int updateTelegramUserId(Long telegramChatId, String telegramUsername) {
        return EMPLOYEE_REPOSITORY.updateTelegramUserId(telegramChatId, telegramUsername);
    }

    @Transactional
    public Long getChatIdByTelegramUsername(String username) {
        return EMPLOYEE_REPOSITORY.getTelegramChatId(username);
    }

    @Transactional
    public Long getChatIdByUserId(Long userId) {
        return EMPLOYEE_REPOSITORY.getTelegramChatIdByUserId(userId);
    }

    public List<Long> getAllChatId() {
        return EMPLOYEE_REPOSITORY.getAllChatId();
    }

    public List<Employee> getEmployeesByDepartmentId(Long departmentId) {
        return EMPLOYEE_REPOSITORY.getByDepartmentId(departmentId);
    }

    public List<Long> getAllChatIdByCompanyId(Long companyId) {
        return EMPLOYEE_REPOSITORY.getAllChatIdByCompanyId(companyId);
    }

    public List<Long> getAllChatIdByDepartmentId(Long departmentId) {
        return EMPLOYEE_REPOSITORY.getAllChatIdByDepartmentId(departmentId);
    }

    public boolean existsById(Long sendTo) {
        return EMPLOYEE_REPOSITORY.existsById(sendTo);
    }

    @Transactional
    public List<UserDTO> getAllUsers(){
        List<Object[]> objectList = EMPLOYEE_REPOSITORY.getAllUsers();
        return mapToDtoList(objectList);
    }

    @Transactional
    public List<UserDTO> getUsersByCompanyId(Long companyId){
        List<Object[]> objectList = EMPLOYEE_REPOSITORY.getUserByCompanyId(companyId);
        return mapToDtoList(objectList);
    }

    public List<UserDTO> mapToDtoList (List<Object[]> objLists) {
        return objLists.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public UserDTO mapToDto(Object[] row){
        UserDTO dto = new UserDTO();
        dto.setName((String) row[0]);
        dto.setEmail((String) row[1]);
        dto.setAddress((String) row[2]);
        dto.setDob((Date) row[3]);
        dto.setGender((Gender) row[4]);
        dto.setNRC((String) row[5]);
        dto.setPassword((String) row[6]);
        dto.setRole((EmployeeRole) row[7]);
        dto.setStatus((EmployeeStatus) row[8]);
        dto.setStuffId((String) row[9]);
        dto.setTelegramUsername((String) row[10]);
        dto.setWorkEntryDate((Date) row[11]);
        dto.setCompanyName((String) row[12]);
        dto.setDepartmentName((String) row[13]);
        dto.setId((Long) row[14]);
        return dto;
    }
}
