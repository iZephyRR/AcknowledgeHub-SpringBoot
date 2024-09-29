package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.SystemDataBean;
import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.*;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.exception_handler.DuplicatedEnteryException;
import com.echo.acknowledgehub.exception_handler.EmailSenderException;
import com.echo.acknowledgehub.exception_handler.UpdatePasswordException;
import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.CompanyRepository;
import com.echo.acknowledgehub.repository.DepartmentRepository;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.echo.acknowledgehub.util.EmailSender;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EmployeeService {
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final AnnouncementRepository ANNOUNCEMENT_REPOSITORY;
    private final ModelMapper MAPPER;
    private final PasswordEncoder PASSWORD_ENCODER;
    private final SystemDataBean SYSTEM_DATA_BEAN;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final FirebaseNotificationService FIREBASE_NOTIFICATION_SERVICE;
    private final CompanyRepository COMPANY_REPOSITORY;
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentRepository DEPARTMENT_REPOSITORY;
    private final EmailSender EMAIL_SENDER;

    @Async
    public CompletableFuture<Optional<Employee>> findById(Long id) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<List<Employee>> getAll() {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.findAll());
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
    public CompletableFuture<StringResponseDTO> findNameByEmail(String email) {
        String name = EMPLOYEE_REPOSITORY.findNameByEmail(email);
        if (name != null) {
            return CompletableFuture.completedFuture(new StringResponseDTO(name));
        } else {
            throw new DataNotFoundException("Cannot find name.");
        }
    }

    @Async
    public CompletableFuture<BooleanResponseDTO> checkPassword(String password) {
        String responsePassword = EMPLOYEE_REPOSITORY.findPasswordById(CHECKING_BEAN.getId());
        return CompletableFuture.completedFuture(new BooleanResponseDTO(PASSWORD_ENCODER.matches(password, responsePassword)));
    }

    @Async
    @Transactional
    public CompletableFuture<Void> updatePassword(ChangePasswordDTO changePasswordDTO) {
        LOGGER.info("Requested change password : " + changePasswordDTO);
        int updatedRows;
        if (changePasswordDTO.getEmail() != null) {
            updatedRows = EMPLOYEE_REPOSITORY.updatePasswordByEmail(changePasswordDTO.getEmail(), PASSWORD_ENCODER.encode(changePasswordDTO.getPassword()));
        } else {
            updatedRows = EMPLOYEE_REPOSITORY.updatePasswordById(CHECKING_BEAN.getId(), PASSWORD_ENCODER.encode(changePasswordDTO.getPassword()));
        }
        LOGGER.info("Updated rows : " + updatedRows);
        if (updatedRows > 0) {
            return CompletableFuture.completedFuture(null);
        } else {
            throw new UpdatePasswordException("Failed to update password.");
        }
    }


    @Async
    @Transactional
    public CompletableFuture<Integer> changeDefaultPassword(String rawPassword) {
        SYSTEM_DATA_BEAN.setDefaultPassword(rawPassword);
        List<EmailAndNameDTO> stringResponseDTOS = EMPLOYEE_REPOSITORY.getDefaultAccountEmailsAndNames();
        stringResponseDTOS.parallelStream().forEach((data)->{
            try {
                EMAIL_SENDER.sendEmail(new EmailDTO(data.getEmail(),"Update: Default Password Change for Acknowledge Hub","Dear "+data.getName()+",<br>" +
                        "<br>" +
                        "I hope this message finds you well.<br>" +
                        "<br>" +
                        "We wanted to inform you of an important update regarding the Acknowledge Hub system. As part of our ongoing security enhancements, we have updated the default password used for accessing the system.<br>" +
                        "<br>" +
                        "Please note the following changes:<br>" +
                        "<br>" +
                        "New Default Password: "+rawPassword+"<br>" +
                        "For your convenience, the username remains the same as before. We recommend that you log in with the new password and update it to a more secure password of your choice as soon as possible.<br>" +
                        "<br>" +
                        "If you encounter any issues or have any questions regarding this update, please do not hesitate to reach out. Our support team is available to assist you.<br>" +
                        "<br>" +
                        "Thank you for your attention to this matter and for your continued cooperation.<br>" +
                        "<br>" +
                        "Best regards,<br>" +
                        "<br>" + CHECKING_BEAN.getName() +
                        "(ADMIN)<br>" +
                        "<a href='http://127.0.0.1:4200/'> Acknowledge Hub </a>",null));
            } catch (IOException e) {
                throw new EmailSenderException("Couldn't send default password change email.");
            }
        });
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.changeDefaultPassword(PASSWORD_ENCODER.encode(rawPassword)));
    }

    @Async
    public CompletableFuture<Boolean> isPasswordDefault(String email) {
        return CompletableFuture.completedFuture(PASSWORD_ENCODER.matches(SYSTEM_DATA_BEAN.getDefaultPassword(), EMPLOYEE_REPOSITORY.findPasswordByEmail(email)));
    }

    @Async
    @Transactional
    public CompletableFuture<Integer> makePasswordAsDefault(Long id) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.updatePasswordById(id, PASSWORD_ENCODER.encode(SYSTEM_DATA_BEAN.getDefaultPassword())));
    }

    @Async
    private CompletableFuture<Employee> save(UserDTO user) throws IOException{
        MAPPER.typeMap(UserDTO.class, Employee.class).addMappings(mapper -> {
            mapper.map(UserDTO::getDepartmentId, (Employee e, Long id) -> e.getDepartment().setId(id));
            mapper.map(UserDTO::getCompanyId, (Employee e, Long id) -> e.getCompany().setId(id));
            if (user.getCompanyName() != null && user.getDepartmentName() != null) {
                mapper.map(UserDTO::getDepartmentName, (Employee e, String name) -> e.getDepartment().setName(name));
                mapper.map(UserDTO::getCompanyName, (Employee e, String name) -> e.getCompany().setName(name));
            }

        });
        Employee employee = MAPPER.map(user, Employee.class);
        LOGGER.info(employee.toString());
        employee.setPassword(PASSWORD_ENCODER.encode(SYSTEM_DATA_BEAN.getDefaultPassword()));
        EMAIL_SENDER.sendEmail(new EmailDTO(employee.getEmail()," Welcome to Acknowledge Hub - Your Login Information","Dear "+employee.getName()+",<br>" +
                "<br>" +
                "I hope this message finds you well.<br>" +
                "<br>" +
                "Welcome to Acknowledge Hub! As part of our commitment to streamlined communication and efficient management, we have created an account for you in our system.<br>" +
                "<br>" +
                "Please find your login details below:<br>" +
                "<br>" +
                "Username: Your email address ("+employee.getEmail()+")<br>" +
                "Temporary Password: "+SYSTEM_DATA_BEAN.getDefaultPassword()+"<br>" +
                "For your security, we recommend that you log in to your account and change your password as soon as possible. If you encounter any issues or have questions, please do not hesitate to contact our support team.<br>" +
                "<br>" +
                "Thank you for your attention to this matter, and we look forward to your active participation in Acknowledge Hub.<br>" +
                "<br>" +
                "Best regards,<br>" +
                "<br>" + CHECKING_BEAN.getName() +
                "("+CHECKING_BEAN.getRole()+")<br>" +
                "<a href='http://127.0.0.1:4200/'> Acknowledge Hub </a>",null));
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(employee));
    }

    @Async
    public CompletableFuture<Employee> save(Employee employee) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(employee));
    }

    @Async
    public CompletableFuture<Employee> saveMainHR(HRDTO mainHRDTO) {
        if (!existsMainHR().join()) {
            final Employee MAIN_HR = new Employee();
            final Optional<Employee> OPTIONAL_EMPLOYEE = EMPLOYEE_REPOSITORY.findById(CHECKING_BEAN.getId());
            final Employee ADMIN;
            final Company COMPANY = COMPANY_SERVICE.save(new Company(mainHRDTO.getCompanyName())).join();
            if (OPTIONAL_EMPLOYEE.isPresent()) {
                ADMIN = OPTIONAL_EMPLOYEE.get();
                ADMIN.setCompany(COMPANY);
                EMPLOYEE_REPOSITORY.save(ADMIN);
            } else {
                throw new DataNotFoundException("Cannot find admin details.");
            }
            MAIN_HR.setName(mainHRDTO.getHrName());
            MAIN_HR.setEmail(mainHRDTO.getHrEmail());
            MAIN_HR.setStaffId(mainHRDTO.getStaffId());
            MAIN_HR.setCompany(COMPANY);
            MAIN_HR.setRole(EmployeeRole.MAIN_HR);
            MAIN_HR.setPassword(PASSWORD_ENCODER.encode(SYSTEM_DATA_BEAN.getDefaultPassword()));
            return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(MAIN_HR));
        } else {
            throw new DuplicatedEnteryException("Main HR account already added.");
        }
    }

    @Async
    public CompletableFuture<List<Employee>> saveAll(UserExcelDTO users) {
        Department department = DEPARTMENT_SERVICE.save(new Department(users.getDepartmentId(), users.getDepartmentName(), users.getCompanyId())).join();
        List<Employee> employees = new ArrayList<>();
        users.getUsers().forEach(user -> {
            user.setDepartmentId(department.getId());
            user.setCompanyId(users.getCompanyId());
            try {
                this.save(user).thenAccept(employees::add);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return CompletableFuture.completedFuture(employees);
    }

//    @Async
//    public CompletableFuture<List<Employee>> updateAll(UserExcelDTO users) {
//        users.setUsers(users.getUsers().parallelStream()
//                .peek(user -> {
//                    if (Objects.isNull(user.getId())) {
//                        user.setPassword(SYSTEM_DATA_BEAN.getDefaultPassword());
//                    }
//                })
//                .collect(Collectors.toList()));
//        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.saveAll(users.getUsers()));
//    }

    @Async
    @Transactional
    public CompletableFuture<List<Employee>> updateAll(List<UserExcelUpdateDTO> users) {
        List<Employee> employees = new ArrayList<>();
        users.parallelStream().forEach(user -> {
            employees.add(EMPLOYEE_REPOSITORY.save(mapToEntity(user)));
        });
        return CompletableFuture.completedFuture(employees);
    }

    public CompletableFuture<Employee> update(UserExcelUpdateDTO user){
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(mapToEntity(user)));
    }

    public Employee mapToEntity(UserExcelUpdateDTO user){
        Employee employee = new Employee();
        Optional<Department> optionalDepartment;
        if (user.getId() != null) {
            employee.setId(user.getId());
            employee.setPhotoLink(EMPLOYEE_REPOSITORY.getPhotoById(user.getId()));
            employee.setPassword(EMPLOYEE_REPOSITORY.findPasswordById(user.getId()));
            employee.setStatus(user.getStatus());
            Department department=DEPARTMENT_REPOSITORY.findByEmployeeId(user.getId());
            if(department!=null){

                employee.setDepartment(department);
                employee.setCompany(COMPANY_REPOSITORY.getByDepartmentId(department.getId()));
            }else{
                employee.setCompany(COMPANY_REPOSITORY.findByEmployeeId(user.getId()));
            }

        }
        employee.setNrc(user.getNrc());
        if(user.getDepartmentId()!=null){
            optionalDepartment = DEPARTMENT_REPOSITORY.findById(user.getDepartmentId());
            optionalDepartment.ifPresent((department)->{
                employee.setDepartment(department);
                employee.setCompany(COMPANY_REPOSITORY.getByDepartmentId(department.getId()));
            });
        }

        employee.setGender(user.getGender());
        employee.setName(user.getName());
        employee.setAddress(user.getAddress());
        employee.setDob(user.getDob());
        employee.setRole(user.getRole());
        employee.setEmail(user.getEmail());
        employee.setTelegramUsername(user.getTelegramUsername());
        employee.setStaffId(user.getStaffId());
        employee.setNotedCount(user.getNotedCount());
        employee.setTelegramUserId(user.getTelegramUserId());
        return employee;
    }

    public Long getEmployeeIdByTelegramUsername(String telegramUsername) {
        return EMPLOYEE_REPOSITORY.getEmployeeIdByTelegramUsername(telegramUsername);
    }

    @Async
    public CompletableFuture<EmployeeProfileDTO> getProfileInfo(Long id) {
        EmployeeProfileDTO employeeProfileDTO = EMPLOYEE_REPOSITORY.getProfileInfo(id);
        if (employeeProfileDTO == null) {
            return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.getAdminProfileInfo(id));
        } else {
            return CompletableFuture.completedFuture(employeeProfileDTO);
        }
    }

    public long countEmployees() {
        return EMPLOYEE_REPOSITORY.count();
    }

    public List<Long> getMainHRAndHRIds() {
        List<EmployeeRole> roles = Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.HR);
        return EMPLOYEE_REPOSITORY.findAllByRole(roles)
                .stream()
                .map(Employee::getId)
                .collect(Collectors.toList());
    }


    @Async
    public CompletableFuture<UniqueFieldsDTO> getUniques() {
        List<String> emails = EMPLOYEE_REPOSITORY.findDistinctEmails();
        List<String> nrcs = EMPLOYEE_REPOSITORY.findDistinctNrc();
        List<String> staffIds = EMPLOYEE_REPOSITORY.findDistinctStaffIds();
        List<String> telegramUsernames = EMPLOYEE_REPOSITORY.findDistinctTelegramUsernames();
        return CompletableFuture.completedFuture(new UniqueFieldsDTO(emails, nrcs, staffIds, telegramUsernames));
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

    //@Transactional
    public List<UserDTO> getAllUsers() {
        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR || CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR_ASSISTANCE) {
            List<EmployeeRole> excludedRoles = Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.MAIN_HR_ASSISTANCE, EmployeeRole.HR, EmployeeRole.HR_ASSISTANCE);
            return mapToDtoList(EMPLOYEE_REPOSITORY.getAllUsers(excludedRoles));
        }
        List<EmployeeRole> excludedRoles = Arrays.asList(EmployeeRole.HR, EmployeeRole.HR_ASSISTANCE);
        return mapToDtoList((EMPLOYEE_REPOSITORY.getAllUsersByCompany(CHECKING_BEAN.getCompanyId(),excludedRoles)));
    }

    @Transactional
    public List<EmployeeNotedDTO> getEmployeeWhoNoted(List<Long> userIdList) {
        Map<Long, LocalDateTime> notedAtStorage = FIREBASE_NOTIFICATION_SERVICE.getNotedAtStorage();
        List<EmployeeNotedDTO> employeeNotedDTOS = new ArrayList<>();
        for (Long userId : userIdList) {
            EmployeeNotedDTO employeeNotedDTO = EMPLOYEE_REPOSITORY.getEmployeeById(userId);
            LocalDateTime notedAt = notedAtStorage.get(userId);
            employeeNotedDTO.setNotedAt(notedAt);
            employeeNotedDTOS.add(employeeNotedDTO);
        }
        return employeeNotedDTOS;
    }

    public int employeeCountByCompany(Long companyId) {
        return EMPLOYEE_REPOSITORY.getEmployeeCountByCompanyId(companyId);
    }

    public Map<Long, Integer> getSelectedAllAnnouncements() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Map<Long, Integer> employeeCountMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info("before announcement ids");
        List<Long> announcementIds = ANNOUNCEMENT_REPOSITORY.getSelectedAllAnnouncements(SelectAll.TRUE);
        for (Long announcementId : announcementIds) {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
                    .whereEqualTo("announcementId", String.valueOf(announcementId))
                    .orderBy("noticeAt", Query.Direction.DESCENDING)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Long userId = document.getLong("userId");
                //LOGGER.info("User ID from Firebase service: " + userId);
                LocalDateTime noticeAt = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("noticeAt")), formatter);
                LocalDateTime timestamp = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("timestamp")), formatter);
                if (noticeAt.isAfter(timestamp)) {
                    CompletableFuture<Employee> comFuEmployee = findById(userId)
                            .thenApply(employee -> employee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
                    Long companyId = comFuEmployee.join().getCompany().getId();
                    employeeCountMap.merge(companyId, 1, Integer::sum);


                }
            }
        }
        return employeeCountMap;
    }

    public Map<String, Integer> getPercentage() throws ExecutionException, InterruptedException {
        Map<String, Integer> notedPercentageMap = new HashMap<>();
        Map<Long, Integer> employeeCountMap = getSelectedAllAnnouncements();
        LOGGER.info("before announcement count");
        int announcementCount = ANNOUNCEMENT_REPOSITORY.getSelectAllCountAnnouncements(SelectAll.TRUE);
        employeeCountMap.forEach((companyId, notedCount) -> {
            String companyName = COMPANY_REPOSITORY.findCompanyNameById(companyId);
            int employeeCount = employeeCountByCompany(companyId);
            int expectedCount = employeeCount * announcementCount;
            int notedPercentage = (notedCount * 100) / expectedCount;
            notedPercentageMap.put(companyName, notedPercentage);
        });
        LOGGER.info("noted % map : " + notedPercentageMap);
        return notedPercentageMap;
    }

    // sub company's announcements
    public Map<Long, Integer> getSubCompanyAnnouncements() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        Map<Long, Integer> employeeCountMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LOGGER.info("before announcement ids");
        List<Long> announcementIds = ANNOUNCEMENT_REPOSITORY.findAnnouncementIdsByEmployeeId(CHECKING_BEAN.getId());
        for (Long announcementId : announcementIds) {
            ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
                    .whereEqualTo("announcementId", String.valueOf(announcementId))
                    .orderBy("noticeAt", Query.Direction.DESCENDING)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get();
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (DocumentSnapshot document : documents) {
                Long userId = document.getLong("userId");
                //LOGGER.info("User ID from Firebase service: " + userId);
                LocalDateTime noticeAt = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("noticeAt")), formatter);
                LocalDateTime timestamp = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("timestamp")), formatter);
                if (noticeAt.isAfter(timestamp)) {
                    CompletableFuture<Employee> comFuEmployee = findById(userId)
                            .thenApply(employee -> employee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
                    Long departmentId = comFuEmployee.join().getDepartment().getId();
                    employeeCountMap.merge(departmentId, 1, Integer::sum);
                }
            }
        }
        return employeeCountMap;
    }

    public Map<String, Integer> getPercentageForEachDepartment() throws ExecutionException, InterruptedException {
        Map<String, Integer> notedPercentageMap = new HashMap<>();
        Map<Long, Integer> employeeCountMap = getSubCompanyAnnouncements();
        LOGGER.info("before announcement count");
        List<Long> announcementIds = ANNOUNCEMENT_REPOSITORY.findAnnouncementIdsByEmployeeId(CHECKING_BEAN.getId());
        int announcementCount = ANNOUNCEMENT_REPOSITORY.getAnnouncementCountByCompanyAndEmployee(CHECKING_BEAN.getId());
        employeeCountMap.forEach((departmentId, notedCount) -> {
            String departmentName = DEPARTMENT_REPOSITORY.findDepartmentNameById(departmentId);
            int employeeCount = employeeCountByDepartment(departmentId);
            int expectedCount = employeeCount * announcementCount;
            LOGGER.info("announcement count : " + announcementCount);
            LOGGER.info("noted count : " + notedCount);
            LOGGER.info("employee count : " + employeeCount);
            LOGGER.info("expect count : " + expectedCount);
            int notedPercentage = (notedCount * 100) / expectedCount;
            notedPercentageMap.put(departmentName, notedPercentage);
        });
        LOGGER.info("get noted % by dept : "+ notedPercentageMap);
        return notedPercentageMap;
    }

    public int employeeCountByDepartment(Long departmentId) {
        return EMPLOYEE_REPOSITORY.getEmployeeCountByDepartmentId(departmentId);
    }

    public void uploadProfileImage(MultipartFile imageFile) throws IOException {
        Employee employee = EMPLOYEE_REPOSITORY.findById(CHECKING_BEAN.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        LOGGER.info("in employee service uploadProfileImage");
        employee.setPhotoLink(imageFile.getBytes());
        EMPLOYEE_REPOSITORY.save(employee);
    }

    // Method to update an employee with duplicate check
    public CompletableFuture<Employee> updateEmployee(Employee employee) {
        return CompletableFuture.supplyAsync(() -> {
            // Optional: Check if employee already exists by ID
            if (EMPLOYEE_REPOSITORY.existsById(employee.getId())) {
                // Update the employee if it exists
                return EMPLOYEE_REPOSITORY.save(employee);
            } else {
                // Handle the case where the employee doesn't exist (optional)
                throw new IllegalArgumentException("Employee does not exist");
            }
        });
    }

    // Method to check if an employee exists by ID
    public CompletableFuture<Boolean> employeeExists(Long employeeId) {
        return CompletableFuture.supplyAsync(() -> EMPLOYEE_REPOSITORY.existsById(employeeId));
    }

//    @Async
//    public CompletableFuture<AnnouncementAndEmployeesDTO> getAnnouncementAndEmployees(Long announcementId, int days) {
//        // Fetch announcement details
//        Announcement announcement = ANNOUNCEMENT_REPOSITORY.findById(announcementId)
//                .orElseThrow(() -> new DataNotFoundException("Announcement not found"));
//
//        // Fetch employee IDs based on the announcement ID and days
//        List<Long> userIdList = FIREBASE_NOTIFICATION_SERVICE.getNotificationsAndMatchWithEmployees(announcementId, days);
//
//        // Fetch employee details
//        List<EmployeeNotedDTO> employees = getEmployeeWhoNoted(userIdList);
//
//        // Combine announcement and employee data
//        AnnouncementAndEmployeesDTO result = new AnnouncementAndEmployeesDTO(announcement, employees);
//        return CompletableFuture.completedFuture(result);
//    }
//

    public List<UserDTO> getUsersByCompanyId(Long companyId) {
        List<Object[]> objectList = EMPLOYEE_REPOSITORY.getUserByCompanyId(companyId);
        return mapToDtoList(objectList);
    }

    public List<UserDTO> mapToDtoList(List<Object[]> objLists) {
        return objLists.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public UserDTO mapToDto(Object[] row) {
        UserDTO dto = new UserDTO();
        dto.setName((String) row[0]);
        dto.setEmail((String) row[1]);
        dto.setAddress((String) row[2]);
        dto.setDob((Date) row[3]);
        dto.setGender((Gender) row[4]);
        dto.setNrc((String) row[5]);
        dto.setPassword((String) row[6]);
        dto.setRole((EmployeeRole) row[7]);
        dto.setStatus((EmployeeStatus) row[8]);
        dto.setStaffId((String) row[9]);
        dto.setTelegramUsername((String) row[10]);
        dto.setPhotoLink((byte[]) row[11]);
        dto.setCompanyName((String) row[12]);
        dto.setDepartmentName((String) row[13]);
        dto.setId((Long) row[14]);
        return dto;
    }

//    public CompletableFuture<List<Employee>> updateUsers(List<UserDTO> users) {
//        List<Employee> employees = new ArrayList<>();
//        users.forEach(user -> {
//            employees.add(save(user).join());
//        });
//        return CompletableFuture.completedFuture(employees);
//    }

    @Async
    public CompletableFuture<List<String>> getEmailsByCompanyId(Long sendTo) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.getEmailsByCompanyId(sendTo));
    }

    @Async
    public CompletableFuture<List<String>> getEmailsByDepartmentId(Long sendTo) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.getEmailsByDepartmentId(sendTo));
    }

    @Async
    public CompletableFuture<List<String>> getEmailsByUserId(Long sendTo) {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.getEmailsByUserId(sendTo));
    }

    @Async
    public CompletableFuture<Boolean> existsMainHR() {
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.existsMainHR());
    }

    public long count() {
        if (CHECKING_BEAN.getRole() == EmployeeRole.HR || CHECKING_BEAN.getRole() == EmployeeRole.HR_ASSISTANCE) {
            return EMPLOYEE_REPOSITORY.countForHR(CHECKING_BEAN.getCompanyId());
        } else {
            return EMPLOYEE_REPOSITORY.count();
        }
    }

    public List<UserDTO> getDescNotedCount() {
        LOGGER.info("in get asc noted Count");
        return mapToDtoListForNotedCount(EMPLOYEE_REPOSITORY.getDescNotedCount());
    }

    public List<UserDTO> mapToDtoListForNotedCount(List<Object[]> objLists) {
        LOGGER.info("in mapToDtoListForNotedCount");
        return objLists.stream().map(this::mapToDtoForNotedCount).collect(Collectors.toList());
    }

    public UserDTO mapToDtoForNotedCount(Object[] row) {
        UserDTO dto = new UserDTO();
        dto.setName((String) row[0]);
        dto.setEmail((String) row[1]);
        dto.setAddress((String) row[2]);
        dto.setDob((Date) row[3]);
        dto.setGender((Gender) row[4]);
        dto.setNrc((String) row[5]);
        dto.setPassword((String) row[6]);
        dto.setRole((EmployeeRole) row[7]);
        dto.setStatus((EmployeeStatus) row[8]);
        dto.setStaffId((String) row[9]);
        dto.setTelegramUsername((String) row[10]);
        dto.setPhotoLink((byte[]) row[11]);
        dto.setCompanyName((String) row[12]);
        dto.setDepartmentName((String) row[13]);
        dto.setId((Long) row[14]);
        dto.setNotedCount((int) row[15]);
        return dto;
    }

    public Employee getEmployeeById(Long id) {
        return EMPLOYEE_REPOSITORY.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    @Async
    public CompletableFuture<Employee> saveAssHR(HRDTO hrAssistance){
        Employee hrAss=new Employee();
        hrAss.setName(hrAssistance.getHrName());
        hrAss.setStaffId(hrAssistance.getStaffId());
        hrAss.setCompany(new Company(CHECKING_BEAN.getCompanyId()));
        hrAss.setEmail(hrAssistance.getHrEmail());
        hrAss.setRole(CHECKING_BEAN.getRole()==EmployeeRole.MAIN_HR?EmployeeRole.MAIN_HR_ASSISTANCE:EmployeeRole.HR_ASSISTANCE);
        hrAss.setPassword(PASSWORD_ENCODER.encode(SYSTEM_DATA_BEAN.getDefaultPassword()));
        return CompletableFuture.completedFuture(EMPLOYEE_REPOSITORY.save(hrAss));
    }

    public boolean hasTelegramUserId (String email) {
        return EMPLOYEE_REPOSITORY.hasTelegramUserId (email);
    }





}
