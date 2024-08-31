package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.ContentType;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.IsSchedule;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.dto.TargetDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.Target;
import com.echo.acknowledgehub.repository.TargetRepository;
import com.echo.acknowledgehub.service.*;
import com.echo.acknowledgehub.util.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("${app.api.base-url}/announcement")
@AllArgsConstructor
public class AnnouncementController {

    private static final Logger LOGGER = Logger.getLogger(AnnouncementController.class.getName());
    private final Map<Long, List<Target>> targetStorage = new HashMap<>();
    private final AnnouncementService ANNOUNCEMENT_SERVICE;
    private final JWTService JWT_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final ModelMapper MODEL_MAPPER;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;
    //private final TelegramService TELEGRAM_SERVICE;
    private final TargetService TARGET_SERVICE;

    @Scheduled(fixedRate = 60000)
    public void checkPendingAnnouncements() throws IOException {
        List<Announcement> pendingAnnouncementsScheduled = ANNOUNCEMENT_SERVICE.findPendingAnnouncementsScheduledForNow(LocalDateTime.now());
        LOGGER.info("in schedule");
        for (Announcement announcement : pendingAnnouncementsScheduled) {
            announcement.setStatus(AnnouncementStatus.APPROVED);
            ANNOUNCEMENT_SERVICE.save(announcement);
            List<Target> targetList = targetStorage.get(announcement.getId());
            TARGET_SERVICE.insertTargetWithNotifications(targetList, announcement);
            List<Long> chatIdsList = List.of();
            for (Target target : targetList) {
                String receiverType = target.getReceiverType().name();
                Long sendTo = target.getSendTo();
                if (receiverType.equals("COMPANY")) {
                    chatIdsList = EMPLOYEE_SERVICE.getAllChatIdByCompanyId(sendTo);
                } else if (receiverType.equals("DEPARTMENT")) {
                    chatIdsList = EMPLOYEE_SERVICE.getAllChatIdByDepartmentId(sendTo);
                }
                //TELEGRAM_SERVICE.sendToTelegram(chatIdsList, announcement.getContentType().getFirstValue(), announcement.getId(), announcement.getPdfLink(), announcement.getTitle(), announcement.getEmployee().getName());
            }
            targetStorage.remove(announcement.getId());
        }
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAnnouncement(
            @ModelAttribute AnnouncementDTO announcementDTO,
            @RequestHeader("Authorization") String authHeader) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<TargetDTO> targetDTOList = objectMapper.readValue(announcementDTO.getTarget(), new TypeReference<List<TargetDTO>>() {
        });

        String token = authHeader.substring(7);
        Long loggedInId = Long.parseLong(JWT_SERVICE.extractId(token));

        CompletableFuture<Employee> conFuEmployee = EMPLOYEE_SERVICE.findById(loggedInId)
                .thenApply(optionalEmployee -> optionalEmployee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
        Optional<AnnouncementCategory> optionalAnnouncementCategory = ANNOUNCEMENT_CATEGORY_SERVICE.findById(announcementDTO.getCategoryId());
        AnnouncementCategory category = optionalAnnouncementCategory.orElse(null);
        validateTargets(targetDTOList); // validate targets are exist or not
        String scheduleOption = announcementDTO.getScheduleOption();
        if (!"later".equals(scheduleOption) && !"now".equals(scheduleOption)) {
            throw new IllegalArgumentException("Invalid option");
        }
        AnnouncementStatus status;
        IsSchedule isSchedule;
            if (announcementDTO.getScheduleOption().equals("later")) {
                status = AnnouncementStatus.PENDING;
                isSchedule = IsSchedule.TRUE;
            } else {
                status = AnnouncementStatus.APPROVED;
                isSchedule = IsSchedule.FALSE;
            }

        announcementDTO.setStatus(status);
        announcementDTO.setIsSchedule(isSchedule);
        String contentType = announcementDTO.getFile().getContentType();
        Announcement entity = MODEL_MAPPER.map(announcementDTO, Announcement.class);
        entity.setEmployee(conFuEmployee.join());
        entity.setCategory(category);
        String url = ANNOUNCEMENT_SERVICE.handleFileUpload(announcementDTO.getFile()); // cloud
        entity.setPdfLink(url);
        assert contentType != null;
        if (contentType.startsWith("audio/")) {
            entity.setContentType(ContentType.AUDIO);
        } else if (contentType.startsWith("video/")) {
            entity.setContentType(ContentType.VIDEO);
        } else if (contentType.startsWith("image/")) {
            entity.setContentType(ContentType.IMAGE);
        } else if (contentType.startsWith("application/pdf")) {
            entity.setContentType(ContentType.PDF);
        } else if (contentType.startsWith("application/vnd.ms-excel") ||
                contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            entity.setContentType(ContentType.EXCEL);
        }
        Announcement announcement = ANNOUNCEMENT_SERVICE.save(entity); // save announcement
        List<Target> targetList = targetDTOList.stream()
                .map(dto -> {
                    Target target = MODEL_MAPPER.map(dto, Target.class);
                    target.setAnnouncement(announcement); // Set the announcement to target
                    return target;
                })
                .toList();
        LOGGER.info("getting target entity list");
//        TARGET_SERVICE.insertTargetWithNotifications(targetList, announcement);
        if (status == AnnouncementStatus.APPROVED) {
            LOGGER.info("announcement status : " + status);
            List<Target> targets = TARGET_SERVICE.saveTargets(targetList);
            handleTargetsAndNotifications(targets, announcement);
            // target save, send notification
            List<Long> chatIdsList = List.of();
            for (Target target : targets) {
                String receiverType = target.getReceiverType().name();
                Long sendTo = target.getSendTo();
                if (receiverType.equals("COMPANY")) {
                    chatIdsList = EMPLOYEE_SERVICE.getAllChatIdByCompanyId(sendTo);
                } else if (receiverType.equals("DEPARTMENT")) {
                    chatIdsList = EMPLOYEE_SERVICE.getAllChatIdByDepartmentId(sendTo);
                }
                //TELEGRAM_SERVICE.sendToTelegram(chatIdsList, announcement.getContentType().getFirstValue(), announcement.getId(), announcement.getPdfLink(), announcement.getTitle(), announcement.getEmployee().getName());
            }
        } else {
            targetStorage.put(announcement.getId(), targetList);
        }
    }
    private void handleTargetsAndNotifications(List<Target> targetList, Announcement announcement) {
        // Insert all targets with notifications in one call
        TARGET_SERVICE.insertTargetWithNotifications(targetList, announcement);
    }


    private void validateTargets(List<TargetDTO> targetDTOList) {
        for (TargetDTO targetDTO : targetDTOList) {
            String receiverType = targetDTO.getReceiverType();
            Long sendTo = targetDTO.getSendTo();
            if ("COMPANY".equals(receiverType)) {
                if (!COMPANY_SERVICE.existsById(sendTo)) {
                    throw new NoSuchElementException("Company does not exist.");
                }
            } else if ("DEPARTMENT".equals(receiverType)) {
                if (!DEPARTMENT_SERVICE.existsById(sendTo)) {
                    throw new NoSuchElementException("Department does not exist.");
                }
            } else if ("EMPLOYEE".equals(receiverType)) {
                if (!EMPLOYEE_SERVICE.existsById(sendTo)) {
                    throw new NoSuchElementException("Employee does not exist.");
                }
            } else {
                throw new IllegalArgumentException("Invalid receiver type: " + receiverType);
            }
        }
    }

    @GetMapping(value = "/aug-to-oct-2024", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Announcement>>> getAnnouncementsForAugToOct2024() {
        Map<String, List<Announcement>> announcementsByMonth = ANNOUNCEMENT_SERVICE.getAnnouncementsForAugToOct2024();
        return ResponseEntity.ok(announcementsByMonth);
    }
    //findall
    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnnouncementDTO>> getAllAnnouncements() {
        return ResponseEntity.ok(ANNOUNCEMENT_SERVICE.getAllAnnouncements());
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countAnnouncements() {
        long count = ANNOUNCEMENT_SERVICE.countAnnouncements();
        return ResponseEntity.ok(count);
    }
}
