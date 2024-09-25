package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.controller.NotificationController;
import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.echo.acknowledgehub.entity.*;
import com.echo.acknowledgehub.repository.TargetRepository;
import com.sun.tools.jconsole.JConsoleContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Cache;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class TargetService {
    private static final Logger LOGGER = Logger.getLogger(TargetService.class.getName());
    private final TargetRepository TARGET_REPOSITORY;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final CompanyService COMPANY_SERVICE;
    private final NotificationController NOTIFICATION_CONTROLLER;
    private final CheckingBean CHECKING_BEAN;
    private final CustomTargetGroupEntityService CUSTOM_TARGET_GROUP_ENTITY_SERVICE;

    @Transactional
    @Async
    public CompletableFuture<Void> insertTargetWithNotifications(List<Target> targets, Announcement announcement) throws ExecutionException, InterruptedException {
        NotificationDTO notificationDTO = buildNotificationDTO(announcement);
        Set<Long> uniqueEmployeeSet = new HashSet<>();
        Map<Long, ReceiverType> employeeReceiverTypeMap = new HashMap<>();

        for (Target target : targets) {
            if (target.getReceiverType() == ReceiverType.COMPANY) {
                Set<Long> companyEmployees = new HashSet<>(EMPLOYEE_SERVICE.findByCompanyId(target.getSendTo()).get());
                updateEmployeeSetAndMap(uniqueEmployeeSet, employeeReceiverTypeMap, companyEmployees, ReceiverType.COMPANY);
            } else if (target.getReceiverType() == ReceiverType.DEPARTMENT) {
                Set<Long> departmentEmployees = new HashSet<>(EMPLOYEE_SERVICE.findByDepartmentId(target.getSendTo()).get());
                updateEmployeeSetAndMap(uniqueEmployeeSet, employeeReceiverTypeMap, departmentEmployees, ReceiverType.DEPARTMENT);
            } else if (target.getReceiverType() == ReceiverType.EMPLOYEE) {
                uniqueEmployeeSet.add(target.getSendTo());
                employeeReceiverTypeMap.put(target.getSendTo(), ReceiverType.EMPLOYEE);
            } else if (target.getReceiverType() == ReceiverType.CUSTOM) {
                List<CustomTargetGroupEntity> groupEntities = CUSTOM_TARGET_GROUP_ENTITY_SERVICE.getAllGroupEntity(target.getSendTo());
                handleCustomGroupEntities(groupEntities, uniqueEmployeeSet, employeeReceiverTypeMap);
            }
        }
        sendNotificationsForEmployees(uniqueEmployeeSet, employeeReceiverTypeMap, notificationDTO);
        return null;
    }

    private void updateEmployeeSetAndMap(Set<Long> uniqueEmployeeSet, Map<Long, ReceiverType> employeeReceiverTypeMap, Set<Long> employeeIds, ReceiverType receiverType) {
        for (Long employeeId : employeeIds) {
            uniqueEmployeeSet.add(employeeId);
            employeeReceiverTypeMap.put(employeeId, receiverType);
        }
    }

    private void handleCustomGroupEntities(List<CustomTargetGroupEntity> groupEntities, Set<Long> uniqueEmployeeSet, Map<Long, ReceiverType> employeeReceiverTypeMap) throws ExecutionException, InterruptedException {
        for (CustomTargetGroupEntity target : groupEntities) {
            if (target.getReceiverType() == ReceiverType.COMPANY) {
                Set<Long> companyEmployees = new HashSet<>(EMPLOYEE_SERVICE.findByCompanyId(target.getSendTo()).get());
                updateEmployeeSetAndMap(uniqueEmployeeSet, employeeReceiverTypeMap, companyEmployees, ReceiverType.COMPANY);
            } else if (target.getReceiverType() == ReceiverType.DEPARTMENT) {
                Set<Long> departmentEmployees = new HashSet<>(EMPLOYEE_SERVICE.findByDepartmentId(target.getSendTo()).get());
                updateEmployeeSetAndMap(uniqueEmployeeSet, employeeReceiverTypeMap, departmentEmployees, ReceiverType.DEPARTMENT);
            } else if (target.getReceiverType() == ReceiverType.EMPLOYEE) {
                uniqueEmployeeSet.add(target.getSendTo());
                employeeReceiverTypeMap.put(target.getSendTo(), ReceiverType.EMPLOYEE);
            }
        }
    }

    private void sendNotificationsForEmployees(Set<Long> employeeSet, Map<Long, ReceiverType> employeeReceiverTypeMap, NotificationDTO notificationDTO) {
        for (Long employeeId : employeeSet) {
            ReceiverType receiverType = employeeReceiverTypeMap.get(employeeId);
            notificationDTO.setUserId(employeeId);
            notificationDTO.setReceiverType(receiverType);
            notificationDTO.setReceiverId(employeeId);
            NotificationDTO employeeNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
            NOTIFICATION_CONTROLLER.saveNotificationInFirebase(employeeNotificationDTO);
            NOTIFICATION_CONTROLLER.sendNotification(employeeNotificationDTO);
        }
    }

    private NotificationDTO cloneNotificationDTO(NotificationDTO original, Long newTargetId) {
        NotificationDTO clone = new NotificationDTO();
        clone.setTitle(original.getTitle());
        clone.setName(original.getName());
        clone.setTargetId(newTargetId);
        clone.setUserId(original.getUserId());
        clone.setAnnouncementId(original.getAnnouncementId());
        clone.setCategoryName(original.getCategoryName());
        // clone.setStatus(original.getStatus());
        clone.setType(original.getType());
        clone.setNoticeAt(original.getNoticeAt());
        clone.setTimestamp(original.getTimestamp());
        clone.setCompanyId(original.getCompanyId());
        String targetName = resolveTargetNameById(original.getUserId());
        clone.setTargetName(targetName); // Add this line
        clone.setDepartmentId(original.getDepartmentId());
        clone.setReceiverType(original.getReceiverType());
        clone.setReceiverId(original.getReceiverId());

        return clone;
    }


    private NotificationDTO buildNotificationDTO(Announcement announcement) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setTitle(announcement.getTitle());
        // notificationDTO.setUserId(CHECKING_BEAN.getId());
        notificationDTO.setName(CHECKING_BEAN.getName());
        notificationDTO.setCategoryName(announcement.getCategory().getName());
        notificationDTO.setAnnouncementId(announcement.getId());
        //notificationDTO.setStatus(NotificationStatus.SEND);
        notificationDTO.setType(NotificationType.NEW);
        notificationDTO.setNoticeAt(LocalDateTime.now());
        notificationDTO.setTimestamp(LocalDateTime.now());
        notificationDTO.setCompanyId(CHECKING_BEAN.getCompanyId());
        return notificationDTO;
    }

    private String resolveTargetNameById(Long targetId) {
        Optional<Employee> employee = EMPLOYEE_SERVICE.findById(targetId).join();
        if (employee.isPresent()) {
            return employee.get().getName();  // Or any other logic to retrieve employee name
        }
        Optional<Department> department = DEPARTMENT_SERVICE.findById(targetId).join();
        if (department.isPresent()) {
            return department.get().getName();  // Retrieve department name if applicable
        }
        CompanyDTO company = COMPANY_SERVICE.findById(targetId).join();
        // if (company.isPresent()) {
        return company.getName();  // Retrieve company name if applicable
        // }
        //return "Unknown";  // Default if none found
    }

    public List<Target> saveTargets(List<Target> entityList) {
        return TARGET_REPOSITORY.saveAll(entityList);
    }

    public List<Target> findByAnnouncement(Announcement announcement) {
        return TARGET_REPOSITORY.findByAnnouncement(announcement);
    }
}
