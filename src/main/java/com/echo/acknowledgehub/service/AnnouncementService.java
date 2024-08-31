package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.ContentType;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.constant.IsSchedule;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.repository.AnnouncementRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnnouncementService {

    private static final Logger LOGGER = Logger.getLogger(AnnouncementService.class.getName());
    private final AnnouncementRepository ANNOUNCEMENT_REPOSITORY;
    private final CloudinaryService CLOUD_SERVICE;

    @Async
    public CompletableFuture<Optional<Announcement>> findById(Long id) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.findById(id));
    }


    public Announcement save(Announcement announcement) throws IOException {
         return ANNOUNCEMENT_REPOSITORY.save(announcement);
    }

    @Async
    public CompletableFuture<List<Announcement>> saveAll(List<Announcement> announcements) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.saveAll(announcements));
    }

    public String handleFileUpload(MultipartFile file) throws IOException {
        Map<String, String> result = CLOUD_SERVICE.upload(file);
        return  result.get("url");
    }

    public List<Announcement> getAnnouncementsForMonth(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ANNOUNCEMENT_REPOSITORY.findAllByDateBetween(startDateTime, endDateTime);
    }

    @Transactional
    public List<AnnouncementDTO> getAllAnnouncements(){
        List<Object[]> objectList = ANNOUNCEMENT_REPOSITORY.getAllAnnouncements();
        return mapToDtoList(objectList);
    }

    public long countAnnouncements() {
        return ANNOUNCEMENT_REPOSITORY.count();
    }

    public List<Announcement> findPendingAnnouncementsScheduledForNow(LocalDateTime now) {
        return ANNOUNCEMENT_REPOSITORY.findByStatusAndScheduledTime( AnnouncementStatus.PENDING, now); // AnnouncementStatus.PENDING
        //return ANNOUNCEMENT_REPOSITORY.findByStatusAndScheduledTime(AnnouncementStatus.PENDING,IsSchedule.TRUE, now); // AnnouncementStatus.PENDING
    }

    public Map<String, List<Announcement>> getAnnouncementsForAugToOct2024() {
        Map<String, List<Announcement>> announcementsByMonth = new LinkedHashMap<>();
        // Define the start and end dates for August, September, and October
        LocalDateTime startOfAugust = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endOfAugust = LocalDateTime.of(2024, 8, 31, 23, 59, 59);

        LocalDateTime startOfSeptember = LocalDateTime.of(2024, 9, 1, 0, 0);
        LocalDateTime endOfSeptember = LocalDateTime.of(2024, 9, 30, 23, 59, 59);

        LocalDateTime startOfOctober = LocalDateTime.of(2024, 10, 1, 0, 0);
        LocalDateTime endOfOctober = LocalDateTime.of(2024, 10, 31, 23, 59, 59);
        // Fetch announcements for each month and add them to the map
        announcementsByMonth.put("August", getAnnouncementsForMonth(startOfAugust, endOfAugust));
        announcementsByMonth.put("September", getAnnouncementsForMonth(startOfSeptember, endOfSeptember));
        announcementsByMonth.put("October", getAnnouncementsForMonth(startOfOctober, endOfOctober));

        return announcementsByMonth;
    }

    public long count() {
        return ANNOUNCEMENT_REPOSITORY.count();
    }

    public List<AnnouncementDTO> mapToDtoList (List<Object[]> objLists) {
        return objLists.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public AnnouncementDTO mapToDto(Object[] row){
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId((Long) row[0]);
        dto.setCreatedAt(LocalDateTime.parse(((LocalDateTime) row[1]).format(DateTimeFormatter.ISO_DATE_TIME)));
        dto.setStatus((AnnouncementStatus) row[2]);
        dto.setTitle((String) row[3]);
        dto.setContentType((ContentType) row[4]);
        dto.setCategoryName((String) row[5]);
        dto.setCreatedBy((String) row[6]);
        dto.setRole((EmployeeRole) row[7]);
        dto.setFileUrl((String) row[8]);
        return dto;
    }


   