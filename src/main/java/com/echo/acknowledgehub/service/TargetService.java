//package com.echo.acknowledgehub.service;
//
//import com.echo.acknowledgehub.constant.NotificationStatus;
//import com.echo.acknowledgehub.constant.NotificationType;
//import com.echo.acknowledgehub.constant.ReceiverType;
//import com.echo.acknowledgehub.controller.NotificationController;
//import com.echo.acknowledgehub.dto.NotificationDTO;
//import com.echo.acknowledgehub.dto.TargetDTO;
//import com.echo.acknowledgehub.entity.*;
//import com.echo.acknowledgehub.repository.TargetRepository;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//@Service
//@AllArgsConstructor
//public class TargetService {
//    private static final Logger LOGGER = Logger.getLogger(TargetService.class.getName());
//    private final TargetRepository TARGET_REPOSITORY;
//    private final EmployeeService EMPLOYEE_SERVICE;
//    private final DepartmentService DEPARTMENT_SERVICE;
//    private final CompanyService COMPANY_SERVICE;
//    private final NotificationController NOTIFICATION_CONTROLLER;  // Inject NotificationController
//
//    @Transactional
//    public synchronized List<Target> insertTargetWithNotifications(List<TargetDTO> targetDTOList, Announcement announcement) {
//        List<Target> targets = new ArrayList<>();
//
//        for (TargetDTO dto : targetDTOList) {
//            try {
//                // Check if the target already exists
//                Optional<Target> existingTargetOptional = TARGET_REPOSITORY.findByAnnouncementAndReceiverTypeAndSendTo(
//                        announcement, ReceiverType.valueOf(dto.getReceiverType()), dto.getSendTo()
//                );
//
//                if (existingTargetOptional.isPresent()) {
//                    // Skip this entry to prevent duplicates
//                    continue;
//                }
//
//
//                Target target = new Target();
//                target.setReceiverType(ReceiverType.valueOf(dto.getReceiverType()));
//                target.setSendTo(dto.getSendTo());
//                target.setAnnouncement(announcement);
//                targets.add(target);
//
//
//            } catch (DataIntegrityViolationException e) {
//                LOGGER.warning("Data integrity violation while saving target: " + e.getMessage());
//                continue;
//            }
//        }
//
//        // Save all unique targets
//        targets = TARGET_REPOSITORY.saveAll(targets);
//
//        // Handle notifications based on the receiver type
//        for (Target target : targets) {
//            switch (target.getReceiverType()) {
//                case EMPLOYEE:
//                    createNotificationForEmployee(target.getSendTo(), announcement, target);
//                    break;
//                case DEPARTMENT:
//                    createNotificationsForDepartment(target.getSendTo(), announcement, target);
//                    break;
//                case COMPANY:
//                    createNotificationsForCompany(target.getSendTo(), announcement, target);
//                    break;
//                case CUSTOM:
//                    // Handle custom logic if necessary
//                    break;
//                default:
//                    LOGGER.warning("Unknown receiver type: " + target.getReceiverType());
//            }
//        }
//
//        return targets;
//    }
//
//    private void createNotificationForEmployee(Long employeeId, Announcement announcement, Target target) {
//        EMPLOYEE_SERVICE.findById(employeeId).thenAccept(optionalEmployee -> {
//            if (optionalEmployee.isPresent()) {
//                NotificationDTO notificationDTO = buildNotificationDTO(announcement, target, optionalEmployee.get().getId());
//                NOTIFICATION_CONTROLLER.sendNotification(notificationDTO, employeeId); // Pass employeeId as loggedInId
//            } else {
//                LOGGER.warning("Employee with ID " + employeeId + " not found.");
//            }
//        }).join();
//    }
//
//
//    private void createNotificationsForDepartment(Long departmentId, Announcement announcement, Target target) {
//        List<Long> employeeIds = EMPLOYEE_SERVICE.findByDepartmentId(departmentId).join();
//        for (Long employeeId : employeeIds) {
//            createNotificationForEmployee(employeeId, announcement, target);
//        }
//    }
//
//    private void createNotificationsForCompany(Long companyId, Announcement announcement, Target target) {
//        List<Long> employeeIds = EMPLOYEE_SERVICE.findByCompanyId(companyId).join();
//        for (Long employeeId : employeeIds) {
//            createNotificationForEmployee(employeeId, announcement, target);
//        }
//    }
//
//    private NotificationDTO buildNotificationDTO(Announcement announcement, Target target, Long employeeId) {
//        NotificationDTO notificationDTO = new NotificationDTO();
//        notificationDTO.setEmployeeId(employeeId);
//        notificationDTO.setAnnouncementId(announcement.getId());
//        notificationDTO.setTargetId(target.getId());
//        notificationDTO.setStatus(NotificationStatus.SEND);
//        notificationDTO.setType(NotificationType.RECEIVED);
//        notificationDTO.setNoticeAt(LocalDateTime.now());
//        return notificationDTO;
//    }
//}
