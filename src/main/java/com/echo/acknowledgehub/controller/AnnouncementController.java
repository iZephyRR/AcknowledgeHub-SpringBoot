package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.dto.NotificationDTO;
import com.echo.acknowledgehub.dto.TargetDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.Target;
import com.echo.acknowledgehub.service.*;
import com.echo.acknowledgehub.util.JWTService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.base-url}/announcement")
@AllArgsConstructor
public class AnnouncementController {

    private static final Logger LOGGER = Logger.getLogger(AnnouncementController.class.getName());
    private final AnnouncementService ANNOUNCEMENT_SERVICE;
    private final JWTService JWT_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final ModelMapper MODEL_MAPPER;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;
    //private final TelegramService TELEGRAM_SERVICE;
    private final CloudinaryServiceImpl CLOUDINARY_SERVICE_IMP;
    private final TargetService TARGET_SERVICE;
    private final NotificationController NOTIFICATION_CONTROLLER;


    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAnnouncement(
                    @ModelAttribute AnnouncementDTO announcementDTO,
                    @RequestHeader("Authorization") String authHeader) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<TargetDTO> targetDTOList = objectMapper.readValue(announcementDTO.getTarget(), new TypeReference<List<TargetDTO>>() {
        });

        String token = authHeader.substring(7);
        Long loggedInId = Long.parseLong(JWT_SERVICE.extractId(token));
        LOGGER.info("LoggedId : " + loggedInId);

        CompletableFuture<Employee> conFuEmployee = EMPLOYEE_SERVICE.findById(loggedInId)
                .thenApply(optionalEmployee -> optionalEmployee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
        Optional<AnnouncementCategory> optionalAnnouncementCategory = ANNOUNCEMENT_CATEGORY_SERVICE.findById(announcementDTO.getCategoryId());
        AnnouncementCategory category = optionalAnnouncementCategory.orElse(null);

        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR || CHECKING_BEAN.getRole() == EmployeeRole.HR) {
            announcementDTO.setStatus(AnnouncementStatus.APPROVED);
        } else {
            announcementDTO.setStatus(AnnouncementStatus.PENDING);
        }
        LOGGER.info("Status : " + announcementDTO.getStatus());

        Announcement entity = MODEL_MAPPER.map(announcementDTO, Announcement.class);
        entity.setEmployee(conFuEmployee.join());
        entity.setCategory(category);
        entity.setCreatedAt(LocalDateTime.now());

        String url = ANNOUNCEMENT_SERVICE.handleFileUpload(announcementDTO.getFile());
        entity.setPdfLink(url);

        Announcement announcement = ANNOUNCEMENT_SERVICE.save(entity); // ann save
        LOGGER.info("after saving announcement");
        List<Target> targetList = targetDTOList.stream()
                .map(dto -> {
                    Target target = MODEL_MAPPER.map(dto, Target.class);
                    target.setAnnouncement(announcement); // Set the announcement
                    return target;
                })
                .toList();
        LOGGER.info("getting target entity list");
        TARGET_SERVICE.insertTargetWithNotifications(targetList, announcement);

//        for (Target target : targetList) {
//            NotificationDTO notificationDTO = new NotificationDTO();
//            notificationDTO.setEmployeeId(loggedInId);
//            notificationDTO.setAnnouncementId(announcement.getId());
////          notificationDTO.setTargetId(target.getId());  // Set the targetId
//            notificationDTO.setStatus(NotificationStatus.SEND);
//            notificationDTO.setType(NotificationType.RECEIVED);
//            notificationDTO.setNoticeAt(LocalDateTime.now());
//            //notificationDTO.setReceiverType(announcement.get); // Set receiverType
//            notificationDTO.setSentTo(target.getSendTo());
//            notificationDTO.setCategoryId(announcement.getCategory().getId());
//            notificationDTO.setTitle(announcement.getTitle());
//            NOTIFICATION_CONTROLLER.sendNotification(notificationDTO, loggedInId);
//        }
        List<Long> chatIdsList = EMPLOYEE_SERVICE.getAllChatId();
        //TELEGRAM_SERVICE.sendReportsInBatches(chatIdsList, announcement.getPdfLink(), announcement.getTitle(), announcement.getEmployee().getName());
    }

    @GetMapping("/aug-to-oct-2024")
    public ResponseEntity<Map<String, List<Announcement>>> getAnnouncementsForAugToOct2024() {
        Map<String, List<Announcement>> announcementsByMonth = ANNOUNCEMENT_SERVICE.getAnnouncementsForAugToOct2024();
        return ResponseEntity.ok(announcementsByMonth);
    }
}