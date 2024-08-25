package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.controller.NotificationController;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.echo.acknowledgehub.dto.TargetDTO;
import com.echo.acknowledgehub.entity.*;
import com.echo.acknowledgehub.repository.TargetRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public synchronized List<Target> insertTargetWithNotifications(List<Target> targetList, Announcement announcement) {
        LOGGER.info("in target service -  insertTargetWithNotifications");

        // Save all unique targets
        LOGGER.info("before saving targets ");
        for (Target target : targetList) {
            LOGGER.info("For Saving Target: " + target.toString());
        }

        List<Target> targets = TARGET_REPOSITORY.saveAll(targetList);
        LOGGER.info("After saving targets ");
      
        // Handle notifications based on the receiver type
        for (Target target : targets) {
            switch (target.getReceiverType()) {
                case EMPLOYEE:

                    LOGGER.info("case employee");
                    createNotificationForEmployee(target.getSendTo(), announcement);
                    break;
                case DEPARTMENT:
                    LOGGER.info("case department");
                    createNotificationsForDepartment(target.getSendTo(), announcement);
                    break;
                case COMPANY:
                    LOGGER.info("case company");
                    createNotificationsForCompany(target.getSendTo(), announcement);
                    break;
                case CUSTOM:
                    // Handle custom logic if necessary
                    break;
                default:
                    LOGGER.warning("Unknown receiver type: " + target.getReceiverType());
            }
        }
        return targets;
    }

    private void createNotificationForEmployee(Long employeeId, Announcement announcement) {
            NotificationDTO notificationDTO = buildNotificationDTO(announcement, employeeId);
            NOTIFICATION_CONTROLLER.sendNotification(notificationDTO, employeeId);
    }


    private void createNotificationsForDepartment(Long departmentId, Announcement announcement) {
        List<Long> employeeIds = EMPLOYEE_SERVICE.findByDepartmentId(departmentId).join();
        for (Long employeeId : employeeIds) {
            LOGGER.info("employee id from department : " + employeeId);
            createNotificationForEmployee(employeeId, announcement);
        }
    }

    private void createNotificationsForCompany(Long companyId, Announcement announcement) {
        List<Long> employeeIds = EMPLOYEE_SERVICE.findByCompanyId(companyId).join();
        for (Long employeeId : employeeIds) {

            LOGGER.info("employee id from company : " + employeeId);
            createNotificationForEmployee(employeeId, announcement);
        }
    }

    private NotificationDTO buildNotificationDTO(Announcement announcement, Long employeeId) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setTitle(announcement.getTitle());
        notificationDTO.setName(String.valueOf(CHECKING_BEAN.getRole()));
        notificationDTO.setUserId(CHECKING_BEAN.getId());
        notificationDTO.setTargetId(employeeId);
        notificationDTO.setAnnouncementId(announcement.getId());
        notificationDTO.setCategoryName(announcement.getCategory().getName());
        //notificationDTO.setTargetId(notificationDTO.getTargetId());
        //notificationDTO.setNotedAt(notificationDTO.getNotedAt());
        notificationDTO.setStatus(NotificationStatus.SEND);
        notificationDTO.setType(NotificationType.RECEIVED);
        notificationDTO.setNoticeAt(LocalDateTime.now());
        return notificationDTO;

    }
    public void saveTargets(List<Target> entityList) {
        TARGET_REPOSITORY.saveAll(entityList);
    }

}
