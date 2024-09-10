package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.*;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.*;
import com.echo.acknowledgehub.service.*;
import com.echo.acknowledgehub.util.CustomMultipartFile;
import com.echo.acknowledgehub.util.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Qualifier;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("${app.api.base-url}/announcement")
@AllArgsConstructor
public class AnnouncementController {

    private static final Logger LOGGER = Logger.getLogger(AnnouncementController.class.getName());
    private final Map<Long, SaveTargetsForSchedule> targetStorage = new HashMap<>();
    private final AnnouncementService ANNOUNCEMENT_SERVICE;
    private final JWTService JWT_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final ModelMapper MODEL_MAPPER;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final CompanyService COMPANY_SERVICE;
    private final DepartmentService DEPARTMENT_SERVICE;
    private final AnnouncementCategoryService ANNOUNCEMENT_CATEGORY_SERVICE;
    private final TelegramService TELEGRAM_SERVICE;
    private final TargetService TARGET_SERVICE;
    private final DraftService DRAFT_SERVICE;

    @Scheduled(fixedRate = 60000)
    public void checkPendingAnnouncements() throws IOException {
        List<Announcement> pendingAnnouncementsScheduled = ANNOUNCEMENT_SERVICE.findPendingAnnouncementsScheduledForNow(LocalDateTime.now());
        LOGGER.info("in schedule");
        for (Announcement announcement : pendingAnnouncementsScheduled) {
            announcement.setStatus(AnnouncementStatus.UPLOADED);
            ANNOUNCEMENT_SERVICE.save(announcement);
            SaveTargetsForSchedule saveTargetsForSchedule = targetStorage.get(announcement.getId());
            List<Target> targetList = saveTargetsForSchedule.getTargets();
            List<String> selectedChannels = saveTargetsForSchedule.getSelectedChannels();
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
                for (String channel : selectedChannels) {
                    if ("Telegram".equalsIgnoreCase(channel)) {
                        LOGGER.info("link : " + announcement.getPdfLink());
                        TELEGRAM_SERVICE.sendToTelegram(chatIdsList, announcement.getContentType().getFirstValue(), announcement.getId(), announcement.getPdfLink(), announcement.getTitle(), announcement.getEmployee().getName());
                    }
                    if ("Email".equalsIgnoreCase(channel)) {
                        // email service
                    }
                }
            }
            targetStorage.remove(announcement.getId());
        }
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAnnouncement(
            @ModelAttribute AnnouncementDTO announcementDTO
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> selectedChannels = objectMapper.readValue(announcementDTO.getChannel(), new TypeReference<List<String>>() {});
        validateChannels(selectedChannels);
        List<TargetDTO> targetDTOList = objectMapper.readValue(announcementDTO.getTarget(), new TypeReference<List<TargetDTO>>() {});
        Long loggedInId = CHECKING_BEAN.getId();
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
            status = AnnouncementStatus.UPLOADED;
            isSchedule = IsSchedule.FALSE;
        }
        announcementDTO.setStatus(status);
        announcementDTO.setIsSchedule(isSchedule);
        Announcement entity = MODEL_MAPPER.map(announcementDTO, Announcement.class);
        entity.setEmployee(conFuEmployee.join());
        entity.setCategory(category);
        if (announcementDTO.isSelectAll()) {
            entity.setSelectAll(SelectAll.TRUE);
        }else {
            entity.setSelectAll(SelectAll.FALSE);
        }
        if (announcementDTO.getFile() == null) {
            LOGGER.info("getting file");
            MultipartFile file = convertToMultipartFile(announcementDTO.getFileUrl(), announcementDTO.getFilename());
            announcementDTO.setFile(file);
        }
        LOGGER.info("content type : " + announcementDTO.getFile().getContentType());
        String contentType = announcementDTO.getFile().getContentType();
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
        } else if (contentType.startsWith("application/x-zip-compressed")) {
            entity.setContentType(ContentType.ZIP);
        }
        Announcement announcement = ANNOUNCEMENT_SERVICE.save(entity); // save announcement
        List<Target> targetList = targetDTOList.stream()
                .map(dto -> {
                    Target target = MODEL_MAPPER.map(dto, Target.class);
                    target.setAnnouncement(announcement); // Set the announcement to target
                    return target;
                })
                .toList();
        if (status == AnnouncementStatus.UPLOADED) {
            LOGGER.info("announcement status : " + status);
            List<Target> targets = TARGET_SERVICE.saveTargets(targetList);
            handleTargetsAndNotifications(targets, announcement); // target save, send notification
            List<Long> chatIdsList = List.of();
            for (Target target : targets) {
                String receiverType = target.getReceiverType().name();
                Long sendTo = target.getSendTo();
                if (receiverType.equals("COMPANY")) {
                    chatIdsList = EMPLOYEE_SERVICE.getAllChatIdByCompanyId(sendTo);
                    LOGGER.info("Chat IDs for COMPANY with ID " + sendTo + ": " + chatIdsList);
                } else if (receiverType.equals("DEPARTMENT")) {
                    chatIdsList = EMPLOYEE_SERVICE.getAllChatIdByDepartmentId(sendTo);
                    LOGGER.info("Chat IDs for DEPARTMENT with ID " + sendTo + ": " + chatIdsList);
                } else if (receiverType.equals(("EMPLOYEE"))) {
                    Long chatId = EMPLOYEE_SERVICE.getChatIdByUserId(sendTo);
                    chatIdsList = List.of(chatId);
                }
                for (String channel : selectedChannels) {
                    if ("Telegram".equalsIgnoreCase(channel)) {
                        TELEGRAM_SERVICE.sendToTelegram(chatIdsList, announcement.getContentType().getFirstValue(), announcement.getId(), announcement.getPdfLink(), announcement.getTitle(), announcement.getEmployee().getName());
                    }
                    if ("Email".equalsIgnoreCase(channel)) {
                        // email service
                    }
                }
            }
        } else {
            LOGGER.info("announcement status : " + status);
            SaveTargetsForSchedule saveTargetsForSchedule = new SaveTargetsForSchedule();
            saveTargetsForSchedule.setTargets(targetList);
            saveTargetsForSchedule.setSelectedChannels(selectedChannels);
            targetStorage.put(announcement.getId(), saveTargetsForSchedule);
        }
    }

    private void handleTargetsAndNotifications(List<Target> targetList, Announcement announcement) {
        TARGET_SERVICE.insertTargetWithNotifications(targetList, announcement);
    }

    private void validateTargets(List<TargetDTO> targetDTOList) {
        for (TargetDTO targetDTO : targetDTOList) {
            ReceiverType receiverType = targetDTO.getReceiverType();
            Long sendTo = targetDTO.getSendTo();
            if (receiverType==ReceiverType.COMPANY) {
                if (!COMPANY_SERVICE.existsById(sendTo)) {
                    throw new NoSuchElementException("Company does not exist.");
                }
            } else if (receiverType==ReceiverType.DEPARTMENT) {
                if (!DEPARTMENT_SERVICE.existsById(sendTo)) {
                    throw new NoSuchElementException("Department does not exist.");
                }
            } else if (receiverType==ReceiverType.EMPLOYEE) {
                if (!EMPLOYEE_SERVICE.existsById(sendTo)) {
                    throw new NoSuchElementException("Employee does not exist.");
                }
            } else {
                throw new IllegalArgumentException("Invalid receiver type: " + receiverType);
            }
        }
    }

    private void validateChannels(List<String> selectedChannels) {
        List<String> allowedChannels = Arrays.asList("telegram", "email");
        for (String channel : selectedChannels) {
            if (!allowedChannels.contains(channel)) {
                throw new IllegalArgumentException("Invalid channel selected: " + channel);
            }
        }
    }

    @PostMapping(value = "/uploadDraft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnouncementDraft> uploadDraft(@ModelAttribute AnnouncementDraftDTO announcementDraftDTO) throws IOException {
        Long loggedInId = CHECKING_BEAN.getId();
        CompletableFuture<Employee> conFuEmployee = EMPLOYEE_SERVICE.findById(loggedInId)
                .thenApply(optionalEmployee -> optionalEmployee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
        Optional<AnnouncementCategory> optionalAnnouncementCategory = ANNOUNCEMENT_CATEGORY_SERVICE.findById(announcementDraftDTO.getCategoryId());
        AnnouncementCategory category = optionalAnnouncementCategory.orElse(null);
        String url = saveFileAndGetUrl(announcementDraftDTO.getFile());
        announcementDraftDTO.setStatus(AnnouncementStatus.EDITING);
        announcementDraftDTO.setFileUrl(url);
        String contentType = announcementDraftDTO.getFile().getContentType();
        if (contentType.startsWith("audio/")) {
            announcementDraftDTO.setContentType(ContentType.AUDIO);
        } else if (contentType.startsWith("video/")) {
            announcementDraftDTO.setContentType(ContentType.VIDEO);
        } else if (contentType.startsWith("image/")) {
            announcementDraftDTO.setContentType(ContentType.IMAGE);
        } else if (contentType.startsWith("application/pdf")) {
            announcementDraftDTO.setContentType(ContentType.PDF);
        } else if (contentType.startsWith("application/vnd.ms-excel") ||
                contentType.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            announcementDraftDTO.setContentType(ContentType.EXCEL);
        } else if (contentType.startsWith("application/x-zip-compressed")) {
            announcementDraftDTO.setContentType(ContentType.ZIP);
        }
        AnnouncementDraft announcementDraft = MODEL_MAPPER.map(announcementDraftDTO, AnnouncementDraft.class);
        announcementDraft.setTarget(Base64.getEncoder().encode(announcementDraftDTO.getTarget().getBytes(StandardCharsets.UTF_8)));
        announcementDraft.setEmployee(conFuEmployee.join());
        announcementDraft.setCategory(category);
        AnnouncementDraft saveAnnouncementDraft = DRAFT_SERVICE.saveDraft(announcementDraft);
        if (saveAnnouncementDraft.getTitle() != null) {
            return ResponseEntity.ok(saveAnnouncementDraft);
        } else {
            throw new IOException();
        }
    }

    @GetMapping(value = "/get-drafts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnnouncementDraftDTO>> getDrafts() {
        Long loggedInId = CHECKING_BEAN.getId();
        return ResponseEntity.ok(DRAFT_SERVICE.getDrafts(loggedInId));
    }

    @GetMapping(value = "/get-all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AnnouncementDTO>> getAllAnnouncements() {
        return ResponseEntity.ok(ANNOUNCEMENT_SERVICE.getAllAnnouncements());
    }

    @GetMapping(value = "/aug-to-oct-2024", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, List<Announcement>>> getAnnouncementsForAugToOct2024() {
        Map<String, List<Announcement>> announcementsByMonth = ANNOUNCEMENT_SERVICE.getAnnouncementsForAugToOct2024();
        return ResponseEntity.ok(announcementsByMonth);
    }

    @GetMapping(value = "getDraftById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnnouncementDraftDTO> getDraftById(@PathVariable("id") Long draftId) {
        AnnouncementDraft announcementDraft = DRAFT_SERVICE.getById(draftId);
        AnnouncementDraftDTO announcementDraftDTO = MODEL_MAPPER.map(announcementDraft, AnnouncementDraftDTO.class);
        announcementDraftDTO.setCategoryId(announcementDraft.getCategory().getId());
        byte[] decodeBytes = Base64.getDecoder().decode(announcementDraft.getTarget());
        announcementDraftDTO.setTarget(new String(decodeBytes, StandardCharsets.UTF_8));
        return ResponseEntity.ok(announcementDraftDTO);
    }

    @DeleteMapping(value = "/delete-draft/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StringResponseDTO> deleteDraft(@PathVariable("id") Long draftId) throws IOException {
        AnnouncementDraft announcementDraft = DRAFT_SERVICE.getById(draftId);
        String filePath = announcementDraft.getFileUrl();
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path); // delete path
        DRAFT_SERVICE.deleteDraft(draftId);
        return ResponseEntity.ok(new StringResponseDTO("Draft Deleted Successfully"));
    }

    private String saveFileAndGetUrl(MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = timestamp + "_" + originalFilename;
            String uploadDir = "./uploads"; // Directory where you want to store uploaded files

            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            return filePath.toAbsolutePath().toString();
        }
        return null;
    }

    // convert To MultipartFile via url
    public MultipartFile convertToMultipartFile(String filePath, String fileName) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist at path: " + filePath);
        }

        byte[] fileBytes;
        try (InputStream inputStream = new FileInputStream(file)) {
            fileBytes = IOUtils.toByteArray(inputStream);
        }

        // Determine the content type
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream"; // Default content type
        }
        return new CustomMultipartFile(fileBytes, fileName, contentType);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAnnouncements() {
        long count = ANNOUNCEMENT_SERVICE.countAnnouncements();
        return ResponseEntity.ok(count);
    }

    @GetMapping(value = "/pieChart" ,  produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Double> getPercentage() throws ExecutionException, InterruptedException {
        return EMPLOYEE_SERVICE.getPercentage();
    }

}

