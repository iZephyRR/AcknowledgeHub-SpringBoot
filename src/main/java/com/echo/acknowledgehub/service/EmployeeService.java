package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.UserNotFoundException;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.echo.acknowledgehub.util.JWTService;
import com.echo.acknowledgehub.util.XlsxReader;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class EmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final ModelMapper MAPPER;
    private final UserDetailsService USER_DETAILS_SERVICE;
    private final JWTService JWT_SERVICE;
    private final PasswordEncoder PASSWORD_ENCODER;

    @Async
    public CompletableFuture<Employee> findById(Long id) {
        Optional<Employee> employee=EMPLOYEE_REPOSITORY.findById(id);
        if(employee.isPresent()){
            return CompletableFuture.completedFuture(employee.get());
        }else {
            throw new UserNotFoundException();
        }
    }

    @Async
    public CompletableFuture<Optional<Employee>> findByEmail(String email) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findByEmail(email));
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

    @Async
    public CompletableFuture<Boolean> isFirstTime(String email) {
        return CompletableFuture.completedFuture(PASSWORD_ENCODER.matches("root",EMPLOYEE_REPOSITORY.getPasswordById(Long.parseLong(USER_DETAILS_SERVICE.loadUserByUsername(email).getUsername()))));
    }
//    @Async
//    public CompletableFuture<List<Employee>> saveAll(MultipartFile users) throws IOException {
//        return XLSX_READER.getEmployees(users.getInputStream()).thenApply(employees -> {
//            EMPLOYEE_REPOSITORY.saveAll(employees);
//            return employees;
//        });
//    }
//@Async
//public CompletableFuture<List<Employee>> saveAll(UsersDTO users) throws IOException {
//    CompletableFuture<Optional<Company>> company = COMPANY_SERVICE.findByName(users.getCompany());
//    CompletableFuture<Optional<Department>> department = DEPARTMENT_SERVICE.findByName(users.getDepartment());
//    return XLSX_READER.getEmployees(users.getXlsx().getInputStream()).thenApply(employees -> {
//        employees.forEach(employee -> {
//            employee.getDepartment().setId(department.thenApply(finalDepartment -> {
//                if(finalDepartment.isPresent()){
//                    return finalDepartment.get().getId();
//                }else{
//                    throw new UserRegistrationException();
//                }
//            }));
//            employee.getCompany().setId(company.thenApply(finalCompany ->{
//                if(finalCompany.isPresent()){
//                    return finalCompany.get().getId();
//                }else {
//                    throw new UserRegistrationException();
//                }
//            }));
//        });
//        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.saveAll(employees));
//    });
//}
//@Async
//public CompletableFuture<List<Employee>> saveAll(UsersDTO users)  {
//    CompletableFuture<Optional<Company>> companyFuture = COMPANY_SERVICE.findByName(users.getCompany());
//    CompletableFuture<Optional<Department>> departmentFuture = DEPARTMENT_SERVICE.findByName(users.getDepartment());
//
//    return companyFuture.thenCombine(departmentFuture, (companyOpt, departmentOpt) -> {
//                if (companyOpt.isEmpty()) {
//                    CompletableFuture<Company> companyFuture2 = COMPANY_SERVICE.save(new Company(users.getCompany()));
//                    companyFuture2.thenCompose(savedCompany ->
//                            departmentOpt.<CompletionStage<Department>>map(CompletableFuture::completedFuture)
//                                    .orElseGet(() ->
//                                            DEPARTMENT_SERVICE.save(new Department(users.getDepartment(), savedCompany.getId()))));
//                }
//
//                Company company = companyOpt.get();
//                Department department = departmentOpt.get();
//
//                try {
//                    return XLSX_READER.getEmployees(users.getXlsx().getInputStream())
//                            .thenApply(employees -> {
//                                employees.forEach(employee -> {
//                                    employee.getCompany().setId(company.getId());
//                                    employee.getDepartment().setId(department.getId());
//                                });
//                                return employees;
//                            });
//                } catch (IOException e) {
//                    LOGGER.severe("Error "+e);
//                    throw new XlsxReaderException(); // Wrap and propagate the IOException
//                }
//            }).thenCompose(employeesFuture -> employeesFuture)
//            .thenApply(EMPLOYEE_REPOSITORY::saveAll);
//}


    @Async
    public CompletableFuture<List<Employee>> findAll() {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findAll());
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
    public Long getChatIdByUsername(String username) {
        return EMPLOYEE_REPOSITORY.getTelegramChatId(username);
    }

    public List<Long> getAllChatId() { return  EMPLOYEE_REPOSITORY.getAllChatId(); }

}
