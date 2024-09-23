package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.*;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.repository.AnnouncementCategoryRepository;
import com.echo.acknowledgehub.repository.AnnouncementRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnnouncementService {

    private static final Logger LOGGER = Logger.getLogger(AnnouncementService.class.getName());
    private final AnnouncementRepository ANNOUNCEMENT_REPOSITORY;
    private final CloudinaryService CLOUD_SERVICE;
    private final CheckingBean CHECKING_BEAN;

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
        String customFileName = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[0];
        LOGGER.info("original file name" + customFileName);// Custom file name you want to use
        Map<String, String> result = CLOUD_SERVICE.upload(file);
        return result.get("url");  // Return the file URL
    }


    public List<Announcement> getAnnouncementsForMonth(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return ANNOUNCEMENT_REPOSITORY.findAllByDateBetween(startDateTime, endDateTime);
    }


    public List<Announcement> getAll() {

        return ANNOUNCEMENT_REPOSITORY.findAll();
    }

    public List<AnnouncementsShowInDashboard> getAllAnnouncementsForDashboard() {
        return ANNOUNCEMENT_REPOSITORY.getAllAnnouncementsForDashboard();
    }

    public AnnouncementsForShowing getAnnouncementById(Long id) {
        AnnouncementsForShowing announcementsForShowing = ANNOUNCEMENT_REPOSITORY.getAnnouncementById(id);
        announcementsForShowing.setAnnouncementResponseCondition(getResponseCondition(id));
        return announcementsForShowing;
    }

    private AnnouncementResponseCondition getResponseCondition(Long announcementId) {
        if (ANNOUNCEMENT_REPOSITORY.existsById(announcementId)) {
            if (Objects.equals(ANNOUNCEMENT_REPOSITORY.getCreator(announcementId), CHECKING_BEAN.getId())) {
                return AnnouncementResponseCondition.CREATOR;
            } else if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR) {
                return AnnouncementResponseCondition.VIEWER;
            } else {
                List<Long> ids=ANNOUNCEMENT_REPOSITORY.canAccess(CHECKING_BEAN.getCompanyId(), CHECKING_BEAN.getDepartmentId(), CHECKING_BEAN.getId());
                if (ids.contains(announcementId)) {
                    return AnnouncementResponseCondition.RECEIVER;
                } else {
                    throw new DataNotFoundException("Post cannot find");
                }
            }
        } else {
            throw new DataNotFoundException("Post cannot find.");
        }
    }

    public CompletableFuture<List<AnnouncementDTO>> getByCompany() {
        if (CHECKING_BEAN.getRole() == EmployeeRole.MAIN_HR) {
            return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getAllAnnouncementsForMainHR(AnnouncementStatus.UPLOADED));
        }else {
            return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getByCompany(CHECKING_BEAN.getCompanyId(),AnnouncementStatus.UPLOADED));
        }
    }

    public long countAnnouncements() {
        return ANNOUNCEMENT_REPOSITORY.count();
    }

    public List<Announcement> findPendingAnnouncementsScheduledForNow(LocalDateTime now) {
        return ANNOUNCEMENT_REPOSITORY.findByStatusAndScheduledTime(AnnouncementStatus.PENDING, now); // AnnouncementStatus.PENDING
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

    public List<Announcement> getAllAnnouncements() {
        System.out.println("Fetching all announcements");
        return ANNOUNCEMENT_REPOSITORY.findAllAnnouncements();
    }


    public Map<String, List<Announcement>> getAnnouncementsGroupedByMonthAndYear(int year) {
        Map<String, List<Announcement>> announcementsByMonth = new LinkedHashMap<>();
        List<Announcement> allAnnouncements = getAllAnnouncements(); // Check this method

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Announcement announcement : allAnnouncements) {
            int announcementYear = announcement.getCreatedAt().getYear();
            if (announcementYear == year) {
                String monthYear = announcement.getCreatedAt().format(formatter);
                announcementsByMonth
                        .computeIfAbsent(monthYear, k -> new ArrayList<>())
                        .add(announcement);
            }
        }

        return announcementsByMonth;
    }

    public long count() {
        return ANNOUNCEMENT_REPOSITORY.count();
    }

    @Transactional
    public List<AnnouncementDTOForShowing> getAnnouncementByReceiverTypeAndId(ReceiverType receiverType, Long
            receiverId) {
        return ANNOUNCEMENT_REPOSITORY.findAnnouncementDTOsByReceiverType(receiverType, receiverId);
    }

    public List<Long> getSelectedAllAnnouncements() {
        return ANNOUNCEMENT_REPOSITORY.getSelectedAllAnnouncements(SelectAll.TRUE);
    }

    @Transactional
    public int getCountSelectAllAnnouncements() {
        return ANNOUNCEMENT_REPOSITORY.getSelectAllCountAnnouncements(SelectAll.TRUE);
    }


    public List<AnnouncementDTO> mapToDtoList(List<Object[]> objLists) {
        return objLists.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public AnnouncementDTO mapToDto(Object[] row) {
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

@Async
public CompletableFuture<Page<List<DataPreviewDTO>>> getMainPreviews(int page, int size) {
    return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getMainPreviews(CHECKING_BEAN.getCompanyId()
            , CHECKING_BEAN.getDepartmentId()
            , CHECKING_BEAN.getId()
            , PageRequest.of(page, size)
    ));
}

    @Async
    public CompletableFuture<Page<List<DataPreviewDTO>>> getSubPreviews(int page, int size) {
        return CompletableFuture.completedFuture(ANNOUNCEMENT_REPOSITORY.getSubPreviews(CHECKING_BEAN.getCompanyId()
                , CHECKING_BEAN.getDepartmentId()
                , CHECKING_BEAN.getId()
                , PageRequest.of(page, size)
        ));
    }

    @Transactional
    public List<ScheduleList> getScheduleList () {
        List<EmployeeRole> roles = Arrays.asList(EmployeeRole.MAIN_HR, EmployeeRole.MAIN_HR_ASSISTANCE,
                EmployeeRole.HR, EmployeeRole.HR_ASSISTANCE);
        List<Long> employeeIds =  ANNOUNCEMENT_REPOSITORY.findEmployeeIdsByRolesAndCompanyId(roles, CHECKING_BEAN.getCompanyId());
        return ANNOUNCEMENT_REPOSITORY.getScheduleListByEmployeeIds(AnnouncementStatus.PENDING, employeeIds);
    }

    public void deleteAnnouncement(Long id) {
        ANNOUNCEMENT_REPOSITORY.delete(findById(id).join().get());
    }

    public Announcement updateTimeAndStatus(Long announcementId) throws IOException {
        CompletableFuture<Announcement> announcementCompletableFuture = findById(announcementId)
                .thenApply(announcement -> announcement.orElseThrow(() -> new NoSuchElementException("Announcement not found")));
        Announcement announcement = announcementCompletableFuture.join();
        announcement.setCreatedAt(LocalDateTime.now());
        announcement.setStatus(AnnouncementStatus.UPLOADED);
        return save(announcement);
    }

}


   