package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.constant.ReceiverType;
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
        NotificationDTO notificationDTO = buildNotificationDTO(announcement);

        // Save the notification once in Firebase
        //NOTIFICATION_CONTROLLER.saveNotificationInFirebase(notificationDTO);

        // Notify the creator
        //NOTIFICATION_CONTROLLER.sendNotification(notificationDTO);

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
                default:
                    LOGGER.warning("Unknown receiver type: " + target.getReceiverType());
            }
        }
        return null;
    }

    private void addNotificationForEmployee(Long employeeId, NotificationDTO notificationDTO) {
            notificationDTO.setUserId(employeeId);
            notificationDTO.setReceiverType(ReceiverType.EMPLOYEE);
            notificationDTO.setReceiverId(employeeId);
            NotificationDTO employeeNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
            NOTIFICATION_CONTROLLER.sendNotification(employeeNotificationDTO);

    }

    private void addNotificationsForDepartment(Long departmentId, NotificationDTO notificationDTO) {
        List<Long> employeeIds = EMPLOYEE_SERVICE.findByDepartmentId(departmentId).join();
        for (Long employeeId : employeeIds) {
            LOGGER.info("employee id from department: " + employeeId);
            // Set the userId to each employee's ID
            notificationDTO.setUserId(employeeId);
            notificationDTO.setReceiverType(ReceiverType.DEPARTMENT);
            notificationDTO.setReceiverId(departmentId);
            NotificationDTO departmentNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
            NOTIFICATION_CONTROLLER.saveNotificationInFirebase(departmentNotificationDTO);
            NOTIFICATION_CONTROLLER.sendNotification(departmentNotificationDTO);
        }
    }

    private void addNotificationsForCompany(Long companyId, NotificationDTO notificationDTO) {
        List<Long> employeeIds = EMPLOYEE_SERVICE.findByCompanyId(companyId).join();
        for (Long employeeId : employeeIds) {
            LOGGER.info("Sending notification to employee ID from company: " + employeeId);
            // Set the userId to each employee's ID
            notificationDTO.setUserId(employeeId);
            notificationDTO.setReceiverType(ReceiverType.COMPANY);
            notificationDTO.setReceiverId(companyId);
            NotificationDTO companyNotificationDTO = cloneNotificationDTO(notificationDTO, employeeId);
            NOTIFICATION_CONTROLLER.saveNotificationInFirebase(companyNotificationDTO); // Save for each employee
            NOTIFICATION_CONTROLLER.sendNotification(companyNotificationDTO); // Send notification for each employee
        }
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
        String targetName = resolveTargetNameById(newTargetId);
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
