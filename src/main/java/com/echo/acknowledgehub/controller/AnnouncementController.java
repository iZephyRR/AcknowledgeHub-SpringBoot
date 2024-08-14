package com.echo.acknowledgehub.controller;
import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.AnnouncementService;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.JWTService;
import com.echo.acknowledgehub.util.UserDetailsServiceImp;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createAnnouncement(@ModelAttribute AnnouncementDTO announcementDTO,
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
        ANNOUNCEMENT_SERVICE.save(entity,announcementDTO.getFile());


    }
}
