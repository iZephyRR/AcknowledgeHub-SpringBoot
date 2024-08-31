package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.controller.NotificationController;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.echo.acknowledgehub.entity.*;
import com.echo.acknowledgehub.repository.TargetRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    @Transactional
    @Async
    public CompletableFuture<Void> insertTargetWithNotifications(List<Target> targets, Announcement announcement) {
        // Create a common NotificationDTO
        NotificationDTO notificationDTO = buildNotificationDTO(announcement, CHECKING_BEAN.getId());

        // Save the notification once in Firebase
        NOTIFICATION_CONTROLLER.saveNotificationInFirebase(notificationDTO);

        // Notify the creator
        NOTIFICATION_CONTROLLER.sendNotification(notificationDTO);

        // Notify each target based on their receiver type
        for (Target target : targets) {
            switch (target.getReceiverType()) {
                case EMPLOYEE:
                    addNotificationForEmployee(target.getSendTo(), notificationDTO);
                    break;
                case DEPARTMENT:
                    addNotificationsForDepartment(target.getSendTo(), notificationDTO);
                    break;
                case COMPANY:
                    addNotificationsForCompany(target.getSendTo(), notificationDTO);
                    break;
//                case CUSTOM:
//                    // Handle custom logic if necessary
//                    break;
                default:
                    LOGGER.warning("Unknown receiver type: " + target.getReceiverType());
            }
        }
        return null;
    }

    private void addNotificationForEmployee(Long employeeId, NotificationDTO notificationDTO) {
        Optional<Employee> optionalEmployee = EMPLOYEE_SERVICE.findById(employeeId).join();
        if (optionalEmployee.isPresent()) {
            NotificationDTO employeeNotificationDTO = cloneNotificationDTO(notificationDTO, optionalEmployee.get().getId());
            NOTIFICATION_CONTROLLER.sendNotification(employeeNotificationDTO);
        }
    }

    private void addNotificationsForDepartment(Long departmentId, NotificationDTO notificationDTO) {
        List<Long> employeeIds = EMPLOYEE_SERVICE.findByDepartmentId(departmentId).join();
        for (Long employeeId : employeeIds) {
            LOGGER.info("employee id from department: " + employeeId);
            NotificationDTO departmentNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
            NOTIFICATION_CONTROLLER.sendNotification(departmentNotificationDTO);
        }
    }

    private void addNotificationsForCompany(Long companyId, NotificationDTO notificationDTO) {
        List<Long> employeeIds = EMPLOYEE_SERVICE.findByCompanyId(companyId).join();
        for (Long employeeId : employeeIds) {
            LOGGER.info("Sending notification to employee ID from company: " + employeeId);
            NotificationDTO companyNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
            companyNotificationDTO.setCompanyId(companyId);
            NOTIFICATION_CONTROLLER.saveNotificationInFirebase(companyNotificationDTO); // Save for each employee
            NOTIFICATION_CONTROLLER.sendNotification(companyNotificationDTO); // Send notification for each employee
        }
    }

    private NotificationDTO buildNotificationDTO(Announcement announcement, Long companyId) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setTitle(announcement.getTitle());
        notificationDTO.setName(CHECKING_BEAN.getName());
        notificationDTO.setCategoryName(announcement.getCategory().getName());
        notificationDTO.setAnnouncementId(announcement.getId());
        notificationDTO.setStatus(NotificationStatus.SEND);
        notificationDTO.setType(NotificationType.RECEIVED);
        notificationDTO.setNoticeAt(LocalDateTime.now());
        notificationDTO.setTimestamp(LocalDateTime.now());
        notificationDTO.setCompanyId(companyId);
        return notificationDTO;
    }

    private NotificationDTO cloneNotificationDTO(NotificationDTO original, Long newTargetId) {
        NotificationDTO clone = new NotificationDTO();
        clone.setTitle(original.getTitle());
        clone.setName(original.getName());
        clone.setTargetId(newTargetId);
        clone.setUserId(original.getUserId());
        clone.setAnnouncementId(original.getAnnouncementId());
        clone.setCategoryName(original.getCategoryName());
        clone.setStatus(original.getStatus());
        clone.setType(original.getType());
        clone.setNoticeAt(original.getNoticeAt());
        clone.setTimestamp(original.getTimestamp());
        clone.setCompanyId(original.getCompanyId());
        return clone;
    }

    public List<Target> saveTargets(List<Target> entityList) {
        return TARGET_REPOSITORY.saveAll(entityList);
    }

    public List<Target> findByAnnouncement(Announcement announcement) {
        return TARGET_REPOSITORY.findByAnnouncement(announcement);
    }
}
