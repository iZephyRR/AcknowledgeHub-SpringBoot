package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.*;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.*;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.repository.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnnouncementService {

    private static final Logger LOGGER = Logger.getLogger(AnnouncementService.class.getName());
    private final AnnouncementRepository ANNOUNCEMENT_REPOSITORY;
    private final CloudinaryService CLOUD_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final CompanyRepository COMPANY_REPOSITORY;
    private final DepartmentRepository DEPARTMENT_REPOSITORY;
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final CustomTargetGroupRepository CUSTOM_TARGET_GROUP_REPOSITORY;
    private final CustomTargetGroupEntityRepository CUSTOM_TARGET_GROUP_ENTITY_REPOSITORY;
    private final CustomTargetGroupEntityService CUSTOM_TARGET_GROUP_ENTITY_SERVICE;

    @Async
    public CompletableFuture<Optional<Announcement>> findById(Long id) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.findById(id));
    }

    public Announcement save(Announcement announcement) throws IOException {
        return ANNOUNCEMENT_REPOSITORY.save(announcement);
    }

    @Async
    public CompletableFuture<List<Announcement>> saveAll(List<Announcement> announcements) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.saveAll(announcements));
    }

    public String handleFileUpload(MultipartFile file) throws IOException {
        String customFileName = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[0];
        LOGGER.info("original file name" + customFileName);// Custom file name you want to use
        Map<String, String> result = CLOUD_SERVICE.upload(file);
        return result.get("url");  // Return the file URL
    }


    public List<Announcement> getAnnouncementsForMonth(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ANNOUNCEMENT_REPOSITORY.findAllByDateBetween(startDateTime, endDateTime);
    }


    public List<Announcement> getAll() {

        return ANNOUNCEMENT_REPOSITORY.findAll();
    }

    public List<AnnouncementsShowInDashboard> getAllAnnouncementsForDashboard() {
        List<EmployeeRole> roles = Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.MAIN_HR_ASSISTANCE,
                EmployeeRole.HR, EmployeeRole.HR_ASSISTANCE);
        List<Long> employeeIds = ANNOUNCEMENT_REPOSITORY.findEmployeeIdsByRolesAndCompanyId(roles, CHECKING_BEAN.getCompanyId());
        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR) {
            return ANNOUNCEMENT_REPOSITORY.getAllAnnouncementsForDashboard(AnnouncementStatus.UPLOADED);
        }
        return ANNOUNCEMENT_REPOSITORY.getAllAnnouncementsForDashboardByCompany(AnnouncementStatus.UPLOADED, employeeIds);
    }

    public AnnouncementsForShowing getAnnouncementById(Long id) throws ExecutionException, InterruptedException {
        AnnouncementsForShowing announcementsForShowing = ANNOUNCEMENT_REPOSITORY.getAnnouncementById(id);
        announcementsForShowing.setToOwnCompany(getToOwnCompany(announcementsForShowing));
        announcementsForShowing.setAnnouncementResponseCondition(getResponseCondition(id));
        return announcementsForShowing;
    }

    private ToOwnCompany getToOwnCompany(AnnouncementsForShowing announcementsForShowing) throws ExecutionException, InterruptedException {
        if (ANNOUNCEMENT_REPOSITORY.existsById(announcementsForShowing.getId())) {
            CompletableFuture<Employee> conFuEmployee = EMPLOYEE_SERVICE.findById(announcementsForShowing.getAnnouncer())
                    .thenApply(optionalEmployee -> optionalEmployee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
            Long companyId = conFuEmployee.get().getCompany().getId();
            LOGGER.info("companyId : " + companyId);
            Optional<Target> target = ANNOUNCEMENT_REPOSITORY.toOwnCompany(companyId, announcementsForShowing.getId());
            if (target.isPresent()) {
                return ToOwnCompany.TRUE;
            } else {
                return ToOwnCompany.FALSE;
            }
        }
        return ToOwnCompany.FALSE;
    }

    private AnnouncementResponseCondition getResponseCondition(Long announcementId) {
        if (ANNOUNCEMENT_REPOSITORY.existsById(announcementId)) {
            Long creatorId = ANNOUNCEMENT_REPOSITORY.getCreator(announcementId);
            if (Objects.equals(creatorId, CHECKING_BEAN.getId()) || ((CHECKING_BEAN.getRole() == EmployeeRole.HR || CHECKING_BEAN.getRole() == EmployeeRole.HR_ASSISTANCE) && Objects.equals(CHECKING_BEAN.getCompanyId(), COMPANY_REPOSITORY.findByEmployeeId(creatorId).getId()))) {
                return AnnouncementResponseCondition.CREATOR;
            } else if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR || CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR_ASSISTANCE) {
                return AnnouncementResponseCondition.VIEWER;
            } else {
                List<Long> ids = ANNOUNCEMENT_REPOSITORY.canAccess(CHECKING_BEAN.getCompanyId(), CHECKING_BEAN.getDepartmentId(), CHECKING_BEAN.getId());
                if (ids.contains(announcementId)) {
                    return AnnouncementResponseCondition.RECEIVER;
                } else {
                    throw new DataNotFoundException("Post cannot find");
                }
            }
        } else {
            throw new DataNotFoundException("Post cannot find.");
        }
    }

    public CompletableFuture<List<AnnouncementDTO>> getByCompany() {
        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR) {
            return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getAllAnnouncementsForMainHR(AnnouncementStatus.UPLOADED));
        } else {
            return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getByCompany(CHECKING_BEAN.getCompanyId(), AnnouncementStatus.UPLOADED));
        }
    }

    public long countAnnouncements() {
        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR || CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR_ASSISTANCE) {
            return ANNOUNCEMENT_REPOSITORY.count();
        } else {
            return ANNOUNCEMENT_REPOSITORY.countByCompany(CHECKING_BEAN.getCompanyId());
        }
    }

    public List<Announcement> findPendingAnnouncementsScheduledForNow(LocalDateTime now) {
        return ANNOUNCEMENT_REPOSITORY.findByStatusAndScheduledTime(AnnouncementStatus.PENDING, now); // AnnouncementStatus.PENDING
        //return ANNOUNCEMENT_REPOSITORY.findByStatusAndScheduledTime(AnnouncementStatus.PENDING,IsSchedule.TRUE, now); // AnnouncementStatus.PENDING
    }

    public Map<String, List<Announcement>> getAnnouncementsForAugToOct2024() {
        Map<String, List<Announcement>> announcementsByMonth = new LinkedHashMap<>();
        // Define the start and end dates for August, September, and October
        LocalDateTime startOfAugust = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endOfAugust = LocalDateTime.of(2024, 8, 31, 23, 59, 59);

        LocalDateTime startOfSeptember = LocalDateTime.of(2024, 9, 1, 0, 0);
        LocalDateTime endOfSeptember = LocalDateTime.of(2024, 9, 30, 23, 59, 59);

        LocalDateTime startOfOctober = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime endOfOctober = LocalDateTime.of(2024, 10, 31, 23, 59, 59);
        // Fetch announcements for each month and add them to the map
        announcementsByMonth.put("August", getAnnouncementsForMonth(startOfAugust, endOfAugust));
        announcementsByMonth.put("September", getAnnouncementsForMonth(startOfSeptember, endOfSeptember));
        announcementsByMonth.put("October", getAnnouncementsForMonth(startOfOctober, endOfOctober));

        return announcementsByMonth;
    }

    public List<Announcement> getAllAnnouncements() {
        System.out.println("Fetching all announcements");
        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR || CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR_ASSISTANCE) {
            return ANNOUNCEMENT_REPOSITORY.findAllAnnouncements();
        }

        return ANNOUNCEMENT_REPOSITORY.findAllAnnouncementsByCompany(CHECKING_BEAN.getCompanyId());
    }

    public Map<String, List<Announcement>> getAnnouncementsGroupedByMonthAndYear(int year) {
        Map<String, List<Announcement>> announcementsByMonth = new LinkedHashMap<>();
        List<Announcement> allAnnouncements = getAllAnnouncements(); // Check this method

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Announcement announcement : allAnnouncements) {
            int announcementYear = announcement.getCreatedAt().getYear();
            if (announcementYear == year) {
                String monthYear = announcement.getCreatedAt().format(formatter);
                announcementsByMonth
                        .computeIfAbsent(monthYear, k -> new ArrayList<>())
                        .add(announcement);
            }
        }
        return announcementsByMonth;
    }

    public long count() {
        return ANNOUNCEMENT_REPOSITORY.count();
    }

    @Transactional
    public List<AnnouncementDTOForShowing> getAnnouncementByReceiverTypeAndId(ReceiverType receiverType, Long
            receiverId) {
        return ANNOUNCEMENT_REPOSITORY.findAnnouncementDTOsByReceiverType(receiverType, receiverId);
    }

    public List<Long> getSelectedAllAnnouncements() {
        return ANNOUNCEMENT_REPOSITORY.getSelectedAllAnnouncements(SelectAll.TRUE);
    }

    @Transactional
    public int getCountSelectAllAnnouncements() {
        return ANNOUNCEMENT_REPOSITORY.getSelectAllCountAnnouncements(SelectAll.TRUE);
    }


    public List<AnnouncementDTO> mapToDtoList(List<Object[]> objLists) {
        return objLists.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public AnnouncementDTO mapToDto(Object[] row) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId((Long) row[0]);
        dto.setCreatedAt(LocalDateTime.parse(((LocalDateTime) row[1]).format(DateTimeFormatter.ISO_DATE_TIME)));
        dto.setStatus((AnnouncementStatus) row[2]);
        dto.setTitle((String) row[3]);
        dto.setContentType((ContentType) row[4]);
        dto.setCategoryName((String) row[5]);
        dto.setCreatedBy((String) row[6]);
        dto.setRole((EmployeeRole) row[7]);
        dto.setFileUrl((String) row[8]);
        return dto;
    }

    @Async
    public CompletableFuture<Page<List<DataPreviewDTO>>> getMainPreviews(int page, int size) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getMainPreviews(CHECKING_BEAN.getCompanyId()
                , CHECKING_BEAN.getDepartmentId()
                , CHECKING_BEAN.getId()
                , PageRequest.of(page, size)
        ));
    }

    @Async
    public CompletableFuture<Page<List<DataPreviewDTO>>> getSubPreviews(int page, int size) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getSubPreviews(CHECKING_BEAN.getCompanyId()
                , CHECKING_BEAN.getDepartmentId()
                , CHECKING_BEAN.getId()
                , PageRequest.of(page, size)
        ));
    }

    @Transactional
    public List<ScheduleList> getScheduleList() {
        List<EmployeeRole> roles = Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.MAIN_HR_ASSISTANCE,
                EmployeeRole.HR, EmployeeRole.HR_ASSISTANCE);
        List<Long> employeeIds = ANNOUNCEMENT_REPOSITORY.findEmployeeIdsByRolesAndCompanyId(roles, CHECKING_BEAN.getCompanyId());
        return ANNOUNCEMENT_REPOSITORY.getScheduleListByEmployeeIds(AnnouncementStatus.PENDING, employeeIds);
    }

    public void deleteAnnouncement(Long id) {
        ANNOUNCEMENT_REPOSITORY.delete(findById(id).join().get());
    }

    public Announcement updateTimeAndStatus(Long announcementId) throws IOException {
        CompletableFuture<Announcement> announcementCompletableFuture = findById(announcementId)
                .thenApply(announcement -> announcement.orElseThrow(() -> new NoSuchElementException("Announcement not found")));
        Announcement announcement = announcementCompletableFuture.join();
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setStatus(AnnouncementStatus.UPLOADED);
        return save(announcement);
    }

    public List<AnnouncementDTOForReport> announcementDTOForReport() {
        List<EmployeeRole> roles = Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.MAIN_HR_ASSISTANCE,
                EmployeeRole.HR, EmployeeRole.HR_ASSISTANCE);
        List<Long> employeeIds = ANNOUNCEMENT_REPOSITORY.findEmployeeIdsByRolesAndCompanyId(roles, CHECKING_BEAN.getCompanyId());
        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR) {
            return ANNOUNCEMENT_REPOSITORY.announcementDTOForReport(AnnouncementStatus.UPLOADED);
        }
        return ANNOUNCEMENT_REPOSITORY.announcementDTOForReportByCompany(AnnouncementStatus.UPLOADED, employeeIds);
    }

    public List<TargetCompany> targetsByAnnouncementId(Long announcementId) {
        List<Target> targets = ANNOUNCEMENT_REPOSITORY.targetsByAnnouncementId(announcementId);
        Map<Long, TargetCompany> companyMap = new HashMap<>();
        for (Target target : targets) {
            Long receiverId = target.getSendTo();
            if (target.getReceiverType() == ReceiverType.COMPANY) {
                Company company = COMPANY_SERVICE.getCompanyById(receiverId)
                        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
                if (company != null) {
                    companyMap.putIfAbsent(company.getId(), new TargetCompany(company.getName(), new ArrayList<>()));
                }
            } else if (target.getReceiverType() == ReceiverType.DEPARTMENT) {
                Department department = DEPARTMENT_SERVICE.getDepartmentById(receiverId);
                if (department != null) {
                    TargetCompany targetCompany = companyMap.computeIfAbsent(department.getCompany().getId(),
                            id -> new TargetCompany(department.getCompany().getName(), new ArrayList<>()));
                    List<TargetDepartment> departments = targetCompany.getDepartments();
                    TargetDepartment targetDepartment = new TargetDepartment(department.getName(), new ArrayList<>());
                    departments.add(targetDepartment);
                }
            } else if (target.getReceiverType() == ReceiverType.EMPLOYEE) {
                Employee employee = EMPLOYEE_SERVICE.getEmployeeById(receiverId);
                if (employee != null) {
                    Department department = DEPARTMENT_SERVICE.getDepartmentById(employee.getDepartment().getId());
                    if (department != null) {
                        TargetCompany targetCompany = companyMap.computeIfAbsent(department.getCompany().getId(),
                                id -> new TargetCompany(department.getCompany().getName(), new ArrayList<>()));
                        List<TargetDepartment> departments = targetCompany.getDepartments();
                        TargetDepartment targetDepartment = departments.stream()
                                .filter(d -> d.getDepartmentName().equals(department.getName()))
                                .findFirst().orElseGet(() -> {
                                    TargetDepartment newDept = new TargetDepartment(department.getName(), new ArrayList<>());
                                    departments.add(newDept);
                                    return newDept;
                                });
                        targetDepartment.getEmployees().add(new TargetEmployee(employee.getName()));
                    }
                }
            } else if (target.getReceiverType() == ReceiverType.CUSTOM) {
                handleCustomTargets(receiverId, companyMap);
            }
        }
        return new ArrayList<>(companyMap.values());
    }

    private void handleCustomTargets(Long receiver, Map<Long, TargetCompany> companyMap) {
        List<CustomTargetGroupEntity> groupEntities = CUSTOM_TARGET_GROUP_ENTITY_SERVICE.getAllGroupEntity(receiver);
        for (CustomTargetGroupEntity target : groupEntities) {
            Long receiverId = target.getSendTo();
            if (target.getReceiverType() == ReceiverType.COMPANY) {
                Company company = COMPANY_SERVICE.getCompanyById(receiverId)
                        .orElseThrow(() -> new EntityNotFoundException("Company not found"));
                if (company != null) {
                    companyMap.putIfAbsent(company.getId(), new TargetCompany(company.getName(), new ArrayList<>()));
                }
            } else if (target.getReceiverType() == ReceiverType.DEPARTMENT) {
                Department department = DEPARTMENT_SERVICE.getDepartmentById(receiverId);
                if (department != null) {
                    TargetCompany targetCompany = companyMap.computeIfAbsent(department.getCompany().getId(),
                            id -> new TargetCompany(department.getCompany().getName(), new ArrayList<>()));
                    List<TargetDepartment> departments = targetCompany.getDepartments();
                    TargetDepartment targetDepartment = new TargetDepartment(department.getName(), new ArrayList<>());
                    departments.add(targetDepartment);
                }
            } else if (target.getReceiverType() == ReceiverType.EMPLOYEE) {
                Employee employee = EMPLOYEE_SERVICE.getEmployeeById(receiverId);
                if (employee != null) {
                    Department department = DEPARTMENT_SERVICE.getDepartmentById(employee.getDepartment().getId());
                    if (department != null) {
                        TargetCompany targetCompany = companyMap.computeIfAbsent(department.getCompany().getId(),
                                id -> new TargetCompany(department.getCompany().getName(), new ArrayList<>()));
                        List<TargetDepartment> departments = targetCompany.getDepartments();
                        TargetDepartment targetDepartment = departments.stream()
                                .filter(d -> d.getDepartmentName().equals(department.getName()))
                                .findFirst().orElseGet(() -> {
                                    TargetDepartment newDept = new TargetDepartment(department.getName(), new ArrayList<>());
                                    departments.add(newDept);
                                    return newDept;
                                });
                        targetDepartment.getEmployees().add(new TargetEmployee(employee.getName()));
                    }
                }
            }
        }
    }

    public NotedDTO getNotedList(Long announcementId, Long beforeSec) throws
            ExecutionException, InterruptedException {
        List<NotedDTO> notedDTOS = ANNOUNCEMENT_REPOSITORY.getReceiver(announcementId);
        Map<Long, Long> longMap = getNotedEmployeeAndDuration(announcementId);
        // NotedDTO responseDTO = configReceiverForNoted(notedDTOS);
        NotedDTO responseDTO = new NotedDTO();
        AtomicDouble allEmployeeCount = new AtomicDouble();
        AtomicDouble allNotedCount = new AtomicDouble();
        responseDTO.setChildPreviews(notedDTOS.parallelStream().peek((notedDTO -> {
            switch (notedDTO.getReceiverType()) {
                case COMPANY -> {
                    notedDTO.setReceiverName(COMPANY_REPOSITORY.findCompanyNameById(notedDTO.getReceiverId()));
                    List<NotedDTO> departmentNotedDTOList = new LinkedList<>();
                    List<Department> departments = DEPARTMENT_REPOSITORY.findByCompanyId(notedDTO.getReceiverId());
                    departments.parallelStream().forEach((department -> {
                        NotedDTO departmentNotedDTO = new NotedDTO();
                        departmentNotedDTO.setReceiverName(department.getName());
                        departmentNotedDTO.setReceiverType(ReceiverType.DEPARTMENT);
                        departmentNotedDTO.setReceiverId(department.getId());
                        AtomicDouble notedCount = new AtomicDouble();
                        longMap.forEach((userId, duration) -> {
                            if (duration != null) {
                                if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(DEPARTMENT_REPOSITORY.findByEmployeeId(userId).getId(), department.getId())) {
                                    notedCount.getAndAdd(1);
                                }
                            }
                        });
                        departmentNotedDTO.setNotedProgress(Double.parseDouble(new DecimalFormat("#.##").format((notedCount.get() / EMPLOYEE_REPOSITORY.getEmployeeCountByDepartmentId(department.getId())) * 100)));
                        List<NotedDTO> employeeNotedDTOList = new LinkedList<>();
                        List<Employee> employees = EMPLOYEE_REPOSITORY.getByDepartmentId(department.getId());
                        employees.parallelStream().forEach((employee -> {
                            allEmployeeCount.getAndAdd(1);
                            NotedDTO employeeNotedDTO = new NotedDTO();
                            employeeNotedDTO.setReceiverName(employee.getName());
                            employeeNotedDTO.setReceiverType(ReceiverType.EMPLOYEE);
                            employeeNotedDTO.setReceiverId(employee.getId());
                            longMap.forEach((userId, duration) -> {
                                if (duration != null) {
                                    if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(userId, employee.getId())) {
                                        allNotedCount.getAndAdd(1);
                                        employeeNotedDTO.setNotedProgress(100);
                                    }
                                } else {
                                    if (Objects.equals(userId, employee.getId())) {
                                        employeeNotedDTO.setNotedProgress(-1);
                                    }
                                }
                            });

                            employeeNotedDTOList.add(employeeNotedDTO);
                        }));
                        departmentNotedDTO.setChildPreviews(employeeNotedDTOList);
                        departmentNotedDTOList.add(departmentNotedDTO);
                    }));
                    notedDTO.setChildPreviews(departmentNotedDTOList);
                    AtomicDouble notedCount = new AtomicDouble();
                    longMap.forEach((userId, duration) -> {
                        if (duration != null && beforeSec != null) {
                            if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(COMPANY_REPOSITORY.findByEmployeeId(userId).getId(), notedDTO.getReceiverId())) {
                                notedCount.getAndAdd(1);
                            }
                        }

                    });
                    notedDTO.setNotedProgress(Double.parseDouble(new DecimalFormat("#.##").format((notedCount.get() / EMPLOYEE_REPOSITORY.getEmployeeCountByCompanyId(notedDTO.getReceiverId())) * 100)));
                    COMPANY_REPOSITORY.findById(notedDTO.getReceiverId()).ifPresent(company -> notedDTO.setReceiverName(company.getName()));
                }
                case DEPARTMENT -> {
                    List<NotedDTO> employeeNotedDTOList = new LinkedList<>();
                    List<Employee> employees = EMPLOYEE_REPOSITORY.getByDepartmentId(notedDTO.getReceiverId());
                    employees.parallelStream().forEach((employee -> {
                        allEmployeeCount.getAndAdd(1);
                        NotedDTO employeeNotedDTO = new NotedDTO();
                        employeeNotedDTO.setReceiverName(employee.getName());
                        employeeNotedDTO.setReceiverType(ReceiverType.EMPLOYEE);
                        employeeNotedDTO.setReceiverId(employee.getId());
                        longMap.forEach((userId, duration) -> {
                            if (duration != null) {
                                if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(userId, employee.getId())) {
                                    allNotedCount.getAndAdd(1);
                                    employeeNotedDTO.setNotedProgress(100);
                                }
                            } else {
                                if (Objects.equals(userId, employee.getId())) {
                                    employeeNotedDTO.setNotedProgress(-1);
                                }
                            }
                        });
                        employeeNotedDTOList.add(employeeNotedDTO);
                    }));
                    notedDTO.setChildPreviews(employeeNotedDTOList);
                    AtomicDouble notedCount = new AtomicDouble();
                    longMap.forEach((userId, duration) -> {
                        if (duration != null) {
                            if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(DEPARTMENT_REPOSITORY.findByEmployeeId(userId).getId(), notedDTO.getReceiverId())) {
                                notedCount.getAndAdd(1);
                            }
                        }
                    });
                    notedDTO.setNotedProgress(Double.parseDouble(new DecimalFormat("#.##").format((notedCount.get() / EMPLOYEE_REPOSITORY.getEmployeeCountByDepartmentId(notedDTO.getReceiverId())) * 100)));
                    DEPARTMENT_REPOSITORY.findById(notedDTO.getReceiverId()).ifPresent(department -> notedDTO.setReceiverName(department.getName()));
                }
                case EMPLOYEE -> {
                    allEmployeeCount.getAndAdd(1);
                    longMap.forEach((userId, duration) -> {
                        if (duration != null) {
                            if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(userId, notedDTO.getReceiverId())) {
                                allNotedCount.getAndAdd(1);
                                notedDTO.setNotedProgress(100);
                            }
                        } else {
                            if (Objects.equals(userId, notedDTO.getReceiverId())) {
                                notedDTO.setNotedProgress(-1);
                            }
                        }
                    });
                    EMPLOYEE_REPOSITORY.findById(notedDTO.getReceiverId()).ifPresent(employee -> notedDTO.setReceiverName(employee.getName()));
                }
                case CUSTOM ->
                        CUSTOM_TARGET_GROUP_REPOSITORY.findById(notedDTO.getReceiverId()).ifPresent(customTargetGroup ->
                                CUSTOM_TARGET_GROUP_ENTITY_REPOSITORY.findByCustomTargetGroup(customTargetGroup).parallelStream().forEach((customTargetGroupEntity -> {
                                    switch (customTargetGroupEntity.getReceiverType()) {
                                        case COMPANY -> {
                                            notedDTO.setReceiverName(COMPANY_REPOSITORY.findCompanyNameById(notedDTO.getReceiverId()));
                                            List<NotedDTO> departmentNotedDTOList = new LinkedList<>();
                                            List<Department> departments = DEPARTMENT_REPOSITORY.findByCompanyId(notedDTO.getReceiverId());
                                            departments.parallelStream().forEach((department -> {
                                                NotedDTO departmentNotedDTO = new NotedDTO();
                                                departmentNotedDTO.setReceiverName(department.getName());
                                                departmentNotedDTO.setReceiverType(ReceiverType.DEPARTMENT);
                                                departmentNotedDTO.setReceiverId(department.getId());
                                                AtomicDouble notedCount = new AtomicDouble();
                                                longMap.forEach((userId, duration) -> {
                                                    if (duration != null) {
                                                        if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(DEPARTMENT_REPOSITORY.findByEmployeeId(userId).getId(), department.getId())) {
                                                            notedCount.getAndAdd(1);
                                                        }
                                                    }
                                                });
                                                departmentNotedDTO.setNotedProgress(Double.parseDouble(new DecimalFormat("#.##").format((notedCount.get() / EMPLOYEE_REPOSITORY.getEmployeeCountByDepartmentId(department.getId())) * 100)));
                                                List<NotedDTO> employeeNotedDTOList = new LinkedList<>();
                                                List<Employee> employees = EMPLOYEE_REPOSITORY.getByDepartmentId(department.getId());
                                                employees.parallelStream().forEach((employee -> {
                                                    allEmployeeCount.getAndAdd(1);
                                                    NotedDTO employeeNotedDTO = new NotedDTO();
                                                    employeeNotedDTO.setReceiverName(employee.getName());
                                                    employeeNotedDTO.setReceiverType(ReceiverType.EMPLOYEE);
                                                    employeeNotedDTO.setReceiverId(employee.getId());
                                                    longMap.forEach((userId, duration) -> {
                                                        if (duration != null) {
                                                            if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(userId, employee.getId())) {
                                                                allNotedCount.getAndAdd(1);
                                                                employeeNotedDTO.setNotedProgress(100);
                                                            }
                                                        } else {
                                                            if (Objects.equals(userId, employee.getId())) {
                                                                employeeNotedDTO.setNotedProgress(-1);
                                                            }
                                                        }
                                                    });
                                                    employeeNotedDTOList.add(employeeNotedDTO);
                                                }));
                                                departmentNotedDTO.setChildPreviews(employeeNotedDTOList);
                                                departmentNotedDTOList.add(departmentNotedDTO);
                                            }));
                                            notedDTO.setChildPreviews(departmentNotedDTOList);
                                            AtomicDouble notedCount = new AtomicDouble();
                                            longMap.forEach((userId, duration) -> {
                                                if (duration != null) {
                                                    if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(COMPANY_REPOSITORY.findByEmployeeId(userId).getId(), notedDTO.getReceiverId())) {
                                                        notedCount.getAndAdd(1);
                                                    }
                                                }
                                            });
                                            notedDTO.setNotedProgress(Double.parseDouble(new DecimalFormat("#.##").format((notedCount.get() / EMPLOYEE_REPOSITORY.getEmployeeCountByCompanyId(notedDTO.getReceiverId())) * 100)));
                                            COMPANY_REPOSITORY.findById(notedDTO.getReceiverId()).ifPresent(company -> notedDTO.setReceiverName(company.getName()));
                                        }
                                        case DEPARTMENT -> {
                                            List<NotedDTO> employeeNotedDTOList = new LinkedList<>();
                                            List<Employee> employees = EMPLOYEE_REPOSITORY.getByDepartmentId(notedDTO.getReceiverId());
                                            employees.parallelStream().forEach((employee -> {
                                                allEmployeeCount.getAndAdd(1);
                                                NotedDTO employeeNotedDTO = new NotedDTO();
                                                employeeNotedDTO.setReceiverName(employee.getName());
                                                employeeNotedDTO.setReceiverType(ReceiverType.EMPLOYEE);
                                                employeeNotedDTO.setReceiverId(employee.getId());
                                                longMap.forEach((userId, duration) -> {
                                                    if (duration != null) {
                                                        if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(userId, employee.getId())) {
                                                            allNotedCount.getAndAdd(1);
                                                            employeeNotedDTO.setNotedProgress(100);
                                                        }
                                                    } else {
                                                        if (Objects.equals(userId, employee.getId())) {
                                                            employeeNotedDTO.setNotedProgress(-1);
                                                        }
                                                    }
                                                });
                                                employeeNotedDTOList.add(employeeNotedDTO);
                                            }));
                                            notedDTO.setChildPreviews(employeeNotedDTOList);
                                            AtomicDouble notedCount = new AtomicDouble();
                                            longMap.forEach((userId, duration) -> {
                                                if (duration != null) {
                                                    if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(DEPARTMENT_REPOSITORY.findByEmployeeId(userId).getId(), notedDTO.getReceiverId())) {
                                                        notedCount.getAndAdd(1);
                                                    }
                                                }
                                            });
                                            notedDTO.setNotedProgress(Double.parseDouble(new DecimalFormat("#.##").format((notedCount.get() / EMPLOYEE_REPOSITORY.getEmployeeCountByDepartmentId(notedDTO.getReceiverId())) * 100)));
                                            DEPARTMENT_REPOSITORY.findById(notedDTO.getReceiverId()).ifPresent(department -> notedDTO.setReceiverName(department.getName()));
                                        }
                                        case EMPLOYEE -> {
                                            allEmployeeCount.getAndAdd(1);
                                            longMap.forEach((userId, duration) -> {
                                                if (duration != null && beforeSec != null) {
                                                    if ((duration > 0 && (duration < beforeSec || beforeSec == 0)) && Objects.equals(userId, notedDTO.getReceiverId())) {
                                                        allNotedCount.getAndAdd(1);
                                                        notedDTO.setNotedProgress(100);
                                                    }
                                                } else {
                                                    if (Objects.equals(userId, notedDTO.getReceiverId())) {
                                                        notedDTO.setNotedProgress(-1);
                                                    }
                                                }
                                            });
                                            EMPLOYEE_REPOSITORY.findById(customTargetGroupEntity.getSendTo()).ifPresent(employee -> {
                                                notedDTO.setReceiverName(employee.getName());
                                                notedDTO.setReceiverType(ReceiverType.EMPLOYEE);
                                            });
                                        }
                                    }
                                })));
            }
        })).toList());
        responseDTO.setNotedProgress(Double.parseDouble(new DecimalFormat("#.##").format((allNotedCount.get() / allEmployeeCount.get()) * 100)));
        if (responseDTO.getChildPreviews().size() == 1) {
            responseDTO = responseDTO.getChildPreviews().get(0);
        } else {
            responseDTO.setReceiverName("All Company");
        }
        return responseDTO;
    }

    private Map<Long, Long> getNotedEmployeeAndDuration(Long announcementId) throws
            ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<Long, Long> longMap = new HashMap<>();
        ApiFuture<QuerySnapshot> future = dbFirestore.collection("notifications")
                .whereEqualTo("announcementId", String.valueOf(announcementId))
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        documents.parallelStream().forEach((document) -> {
            Long userId = document.getLong("userId");
            LocalDateTime noticeAt = LocalDateTime.parse(
                    Objects.requireNonNull(document.getString("noticeAt")), formatter);
            LocalDateTime timestamp = LocalDateTime.parse(
                    Objects.requireNonNull(document.getString("timestamp")), formatter);
            LocalDateTime deadline = ANNOUNCEMENT_REPOSITORY.getDeadline(announcementId);
            if (deadline != null) {
                if (Duration.between(deadline, noticeAt).getSeconds() > 0) {
                    longMap.put(userId, null);
                } else {
                    longMap.put(userId, Duration.between(timestamp, noticeAt).getSeconds());
                }
            } else {
                longMap.put(userId, Duration.between(timestamp, noticeAt).getSeconds());
            }

        });
        LOGGER.info(longMap.toString());
        return longMap;
    }

    public void updateVersionRelated( Announcement announcement){
        announcement.setVersionRelatedTo(announcement.getId());
        ANNOUNCEMENT_REPOSITORY.save(announcement);
    }

    public int nextVersion(Long id) {
        return ANNOUNCEMENT_REPOSITORY.lastVersion(id) + 1;
    }

    public List<AnnouncementDTOForReport> getByCustomGroup(Long id) {
        return ANNOUNCEMENT_REPOSITORY.getByCustomGroup(id);
    }


}

//    private NotedDTO configReceiverForNoted(List<NotedDTO> notedDTOS) {
//        AtomicLong companyCount = new AtomicLong();
//        NotedDTO responseNotedDTO = new NotedDTO();
//        List<Long> companyIds = new LinkedList<>();
//        notedDTOS.parallelStream().forEach(notedDTO -> {
//            switch (notedDTO.getReceiverType()) {
//                case COMPANY -> {
//                    if (!companyIds.contains(notedDTO.getReceiverId())) {
//                        companyIds.add(notedDTO.getReceiverId());
//                        companyCount.getAndIncrement();
//                    }
//                }
//                case DEPARTMENT -> {
//                    Long companyId = COMPANY_REPOSITORY.getByDepartmentId(notedDTO.getReceiverId()).getId();
//                    if (!companyIds.contains(companyId)) {
//                        companyIds.add(companyId);
//                        companyCount.getAndIncrement();
//                    }
//                }
//                case EMPLOYEE -> {
//                    Long companyId = COMPANY_REPOSITORY.findByEmployeeId(notedDTO.getReceiverId()).getId();
//                    if (!companyIds.contains(companyId)) {
//                        companyIds.add(companyId);
//                        companyCount.getAndIncrement();
//                    }
//                }
//                case CUSTOM ->
//                        CUSTOM_TARGET_GROUP_REPOSITORY.findById(notedDTO.getReceiverId()).ifPresent(customTargetGroup ->
//                                CUSTOM_TARGET_GROUP_ENTITY_REPOSITORY.findByCustomTargetGroup(customTargetGroup).parallelStream().forEach(customTargetGroupEntity -> {
//                                            switch (customTargetGroupEntity.getReceiverType()) {
//                                                case COMPANY -> {
//                                                    if (!companyIds.contains(notedDTO.getReceiverId())) {
//                                                        companyIds.add(notedDTO.getReceiverId());
//                                                        companyCount.getAndIncrement();
//                                                    }
//                                                }
//                                                case DEPARTMENT -> {
//                                                    Long companyId = COMPANY_REPOSITORY.getByDepartmentId(notedDTO.getReceiverId()).getId();
//                                                    if (!companyIds.contains(companyId)) {
//                                                        companyIds.add(companyId);
//                                                        companyCount.getAndIncrement();
//                                                    }
//                                                }
//                                                case EMPLOYEE -> {
//                                                    Long companyId = COMPANY_REPOSITORY.findByEmployeeId(notedDTO.getReceiverId()).getId();
//                                                    if (!companyIds.contains(companyId)) {
//                                                        companyIds.add(companyId);
//                                                        companyCount.getAndIncrement();
//                                                    }
//                                                }
//                                            }
//                                        }
//                                )
//                        );
//            }
//        });
//
//        if (companyCount.get() > 1) {
//           // responseNotedDTO.setReceiverType(ReceiverType.COMPANY);
//            responseNotedDTO.setReceiverName("All Company");
//        }else{
//            responseNotedDTO.setReceiverType(ReceiverType.COMPANY);
//            responseNotedDTO.setReceiverName(COMPANY_REPOSITORY.findCompanyNameById(companyIds.get(0)));
//        }
//        return responseNotedDTO;
//    }
//}


   