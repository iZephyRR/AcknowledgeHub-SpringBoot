package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.*;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.BooleanResponseDTO;
import com.echo.acknowledgehub.dto.ChangePasswordDTO;
import com.echo.acknowledgehub.dto.StringResponseDTO;
import com.echo.acknowledgehub.dto.UserDTO;

import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.exception_handler.UpdatePasswordException;
import com.echo.acknowledgehub.repository.AnnouncementRepository;
import com.echo.acknowledgehub.repository.CompanyRepository;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final FirebaseNotificationService FIREBASE_NOTIFICATION_SERVICE;
    private final CompanyRepository COMPANY_REPOSITORY;

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
    public List<EmployeeNotedDTO> getEmployeeWhoNoted (List<Long> userIdList){
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
                LOGGER.info("User ID from Firebase service: " + userId);
                LocalDateTime noticeAt = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("noticeAt")), formatter);
                LocalDateTime timestamp = LocalDateTime.parse(
                        Objects.requireNonNull(document.getString("timestamp")), formatter);
                if (noticeAt.isAfter(timestamp)) {
                    CompletableFuture<Employee> comFuEmployee = findById(userId)
                            .thenApply(employee -> employee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
                    Long companyId = comFuEmployee.join().getCompany().getId();
                    employeeCountMap.merge(companyId,1,Integer::sum);


                }
            }
        }
        return employeeCountMap;
    }

    public Map<String, Double> getPercentage() throws ExecutionException, InterruptedException {
        Map<String, Double> notedPercentageMap = new HashMap<>();
        Map<Long,Integer> employeeCountMap = getSelectedAllAnnouncements();
        LOGGER.info("before announcement count");
        int announcementCount = ANNOUNCEMENT_REPOSITORY.getSelectAllCountAnnouncements(SelectAll.TRUE);
        employeeCountMap.forEach((companyId,notedCount)-> {
            String companyName = COMPANY_REPOSITORY.findCompanyNameById(companyId);
            int employeeCount = employeeCountByCompany(companyId);
            int expectedCount = employeeCount * announcementCount;
            double notedPercentage = (double) (notedCount * 100) /expectedCount;
            notedPercentageMap.put(companyName,notedPercentage);
        });
        return notedPercentageMap;
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
        return dto;
    }
}
