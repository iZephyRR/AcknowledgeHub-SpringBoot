package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.controller.NotificationController;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.echo.acknowledgehub.entity.*;
import com.echo.acknowledgehub.repository.TargetRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Cache;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
    public CompletableFuture<Void> insertTargetWithNotifications(List<Target> targets, Announcement announcement) {
        NotificationDTO notificationDTO = buildNotificationDTO(announcement);
        for (Target target : targets) {
            ReceiverType receiverType = target.getReceiverType();
            Long sendTo = target.getSendTo();
            Set<NotificationDTO> notificationDTOSet = new HashSet<>();
            if (receiverType == ReceiverType.COMPANY) {

            } else if (receiverType == ReceiverType.DEPARTMENT) {

            } else if (receiverType == ReceiverType.EMPLOYEE) {

            } else if(receiverType == ReceiverType.CUSTOM) {
                List<CustomTargetGroupEntity> groupEntities = CUSTOM_TARGET_GROUP_ENTITY_SERVICE.getAllGroupEntity(sendTo);

            }
        }
        return null;
    }

    private void cloneAndSendNotifications (NotificationDTO notificationDTO) {
        NotificationDTO cloneNotificationDTO = cloneNotificationDTO(notificationDTO);
        NOTIFICATION_CONTROLLER.saveNotificationInFirebase(cloneNotificationDTO);
        NOTIFICATION_CONTROLLER.sendNotification(cloneNotificationDTO);
    }

    private CompletableFuture<Void> addNotificationForEmployee(Long employeeId, NotificationDTO notificationDTO) {
        return EMPLOYEE_SERVICE.findById(employeeId)
                .thenAccept(optionalEmployee -> {
                    optionalEmployee.ifPresentOrElse(employee -> {
                        LOGGER.info("Sending notification to employee ID: " + employeeId);
                        notificationDTO.setUserId(employeeId);
                        notificationDTO.setReceiverType(ReceiverType.EMPLOYEE);
                        notificationDTO.setReceiverId(employeeId);

//                        NotificationDTO employeeNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
//                        NOTIFICATION_CONTROLLER.saveNotificationInFirebase(employeeNotificationDTO);
//                        NOTIFICATION_CONTROLLER.sendNotification(employeeNotificationDTO);
                    }, () -> {
                        LOGGER.warning("Employee not found for ID: " + employeeId);
                    });
                })
                .exceptionally(ex -> {
                    LOGGER.severe("Error sending notification to employee ID " + employeeId + ": " + ex.getMessage());
                    return null;
                });
    }

    private void addNotificationsForDepartment(Long departmentId, NotificationDTO notificationDTO) {
        EMPLOYEE_SERVICE.findByDepartmentId(departmentId)
                .thenAccept(employeeIds -> {
                    for (Long employeeId : employeeIds) {
                        LOGGER.info("employee id from department: " + employeeId);
                        // Set the userId to each employee's ID
                        notificationDTO.setUserId(employeeId);
                        notificationDTO.setReceiverType(ReceiverType.DEPARTMENT);
                        notificationDTO.setReceiverId(departmentId);
//                        NotificationDTO departmentNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
//                        NOTIFICATION_CONTROLLER.saveNotificationInFirebase(departmentNotificationDTO);
//                        NOTIFICATION_CONTROLLER.sendNotification(departmentNotificationDTO);
                    }
                }).exceptionally(ex -> {
                    LOGGER.severe("Error sending notifications for company: " + ex.getMessage());
                    return null;
                });
    }

    private void addNotificationsForCompany(Long companyId, NotificationDTO notificationDTO) {
        EMPLOYEE_SERVICE.findByCompanyId(companyId)
                .thenAccept(employeeIds -> {
                    LOGGER.info("Sending notification to : " + employeeIds);
                    for (Long employeeId : employeeIds) {
                        LOGGER.info("Sending notification to employee ID from company: " + employeeId);
                        notificationDTO.setUserId(employeeId);
                        notificationDTO.setReceiverType(ReceiverType.COMPANY);
                        notificationDTO.setReceiverId(companyId);

//                        NotificationDTO companyNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
//                        NOTIFICATION_CONTROLLER.saveNotificationInFirebase(companyNotificationDTO);
//                        NOTIFICATION_CONTROLLER.sendNotification(companyNotificationDTO);
                    }
                }).exceptionally(ex -> {
                    LOGGER.severe("Error sending notifications for company: " + ex.getMessage());
                    return null;
                });
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

    private NotificationDTO cloneNotificationDTO(NotificationDTO original) {
        NotificationDTO clone = new NotificationDTO();
        clone.setTitle(original.getTitle());
        clone.setName(original.getName());
        clone.setTargetId(original.getUserId());
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

    private String resolveTargetNameById(Long targetId) {
        Optional<Employee> employee = EMPLOYEE_SERVICE.findById(targetId).join();
        if (employee.isPresent()) {
            return employee.get().getName();  // Or any other logic to retrieve employee name
        }
        Optional<Department> department = DEPARTMENT_SERVICE.findById(targetId).join();
        if (department.isPresent()) {
            return department.get().getName();  // Retrieve department name if applicable
        }
        Optional<Company> company = COMPANY_SERVICE.findById(targetId).join();
        if (company.isPresent()) {
            return company.get().getName();  // Retrieve company name if applicable
        }
        return "Unknown";  // Default if none found
    }

    public List<Target> saveTargets(List<Target> entityList) {
        return TARGET_REPOSITORY.saveAll(entityList);
    }

    public List<Target> findByAnnouncement(Announcement announcement) {
        return TARGET_REPOSITORY.findByAnnouncement(announcement);
    }
}
