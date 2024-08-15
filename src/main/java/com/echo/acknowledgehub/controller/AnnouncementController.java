package com.echo.acknowledgehub.controller;
import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.dto.TargetDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.Target;
import com.echo.acknowledgehub.service.*;
import com.echo.acknowledgehub.util.JWTService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

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
    private final TelegramService TELEGRAM_SERVICE;
    private final CloudinaryServiceImpl CLOUDINARY_SERVICE_IMP;
    private final TargetService TARGET_SERVICE;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAnnouncement(@ModelAttribute AnnouncementDTO announcementDTO,
                                   @RequestBody List<TargetDTO> targetDTO,
                                   @RequestHeader("Authorization") String authHeader) throws IOException {
        String token = authHeader.substring(7);
        Long loggedInId = Long.parseLong(JWT_SERVICE.extractId(token));
        LOGGER.info("LoggedId : " + loggedInId);
        CompletableFuture<Employee> conFuEmployee = EMPLOYEE_SERVICE.findById(loggedInId)
                .thenApply(optionalEmployee -> optionalEmployee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
        Optional<AnnouncementCategory> optionalAnnouncementCategory = ANNOUNCEMENT_CATEGORY_SERVICE.findById(announcementDTO.getCategoryId());
        AnnouncementCategory category = null;
        if (optionalAnnouncementCategory.isPresent()) {
            category = optionalAnnouncementCategory.get();
        }
        if(CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR || CHECKING_BEAN.getRole() == EmployeeRole.ADMIN){
            announcementDTO.setStatus("APPROVED");
        } else {
            announcementDTO.setStatus("PENDING");
        }
        LOGGER.info("Status : " + announcementDTO.getStatus());
        Announcement entity = MODEL_MAPPER.map(announcementDTO, Announcement.class);
        entity.setEmployee(conFuEmployee.join());
        entity.setCategory(category);
        entity.setCreatedAt(LocalDateTime.now());
        String url = ANNOUNCEMENT_SERVICE.handleFileUpload(announcementDTO.getFile());
        entity.setPdfLink(url);
        Announcement announcement = ANNOUNCEMENT_SERVICE.save(entity);
        List<Target> targetList = Collections.singletonList(MODEL_MAPPER.map(targetDTO, Target.class));
        for (Target target : targetList){ target.setAnnouncement(announcement); }
        TARGET_SERVICE.insertTarget(targetList);
        List<Long> chatIdsList = EMPLOYEE_SERVICE.getAllChatId();
        TELEGRAM_SERVICE.sendReportsInBatches(chatIdsList,announcement.getPdfLink(), announcement.getTitle(),announcement.getEmployee().getName() );

    }
}
