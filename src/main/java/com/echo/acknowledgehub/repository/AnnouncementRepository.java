package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.*;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<Announcement> findAllByDateBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT a FROM Announcement a WHERE a.status = :status AND a.createdAt <= :now")
    List<Announcement> findByStatusAndScheduledTime(@Param("status") AnnouncementStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT a.id, a.createdAt, a.status, a.title, a.contentType, c.name as categoryName, e.name as creator,e.role as role,a.pdfLink FROM Announcement a " +
            "JOIN a.employee e JOIN a.category c order by a.createdAt DESC")
    List<Object[]> getAllAnnouncements();

    @Query("SELECT new com.echo.acknowledgehub.dto.AnnouncementDTO(a.id, a.title, a.createdAt, a.status, a.category.name, a.employee.name) FROM Announcement a WHERE a.employee.company.id = :id ORDER BY a.createdAt DESC")
    List<AnnouncementDTO> getByCompany(@Param("id") Long id);

    @Query("SELECT new com.echo.acknowledgehub.dto.AnnouncementDTO(a.id, a.title, a.createdAt, a.status, a.category.name, a.employee.name) FROM Announcement a ORDER BY a.createdAt DESC")
    List<AnnouncementDTO> getAllAnnouncementsForMainHR();

    @Query("SELECT a FROM Announcement a WHERE a.status = :status AND a.isSchedule = :isSchedule AND a.createdAt <= :now")
    List<Announcement> findByStatusAndScheduledTime(
            @Param("status") AnnouncementStatus status,
            @Param("isSchedule") IsSchedule isSchedule,
            @Param("now") LocalDateTime now
    );


    @Query("SELECT new com.echo.acknowledgehub.dto.AnnouncementDTOForShowing(a.id, a.title, a.contentType, a.pdfLink) " +
            "FROM Target t " +
            "JOIN t.announcement a " +
            "WHERE t.receiverType = :receiverType AND t.sendTo = :receiverId")
    List<AnnouncementDTOForShowing> findAnnouncementDTOsByReceiverType(@Param("receiverType") ReceiverType receiverType,
                                                                       @Param("receiverId") Long receiverId);

    @Query("select id from Announcement where selectAll=:selectAll")
    List<Long> getSelectedAllAnnouncements(@Param("selectAll") SelectAll selectAll);

    @Query("select count(a) from Announcement a where a.selectAll=:selectAll")
    int getSelectAllCountAnnouncements(@Param("selectAll") SelectAll selectAll);

    @Query("SELECT new com.echo.acknowledgehub.dto.DataPreviewDTO(t.announcement.id, t.announcement.title) " +
            "FROM Target t " +
            "LEFT JOIN CustomTargetGroupEntity c ON c.customTargetGroup.id = t.sendTo " +
            "WHERE (t.announcement.employee.role = 'MAIN_HR' OR t.announcement.employee.role = 'MAIN_HR_ASSISTANCE') " +
            "AND ((t.receiverType = 'COMPANY' AND t.sendTo = :companyId) " +
            "OR (t.receiverType = 'DEPARTMENT' AND t.sendTo = :departmentId) " +
            "OR (t.receiverType = 'EMPLOYEE' AND t.sendTo = :employeeId) " +
            "OR (t.receiverType = 'CUSTOM' AND " +
            "((c.receiverType = 'COMPANY' AND c.sendTo = :companyId) " +
            "OR (c.receiverType = 'DEPARTMENT' AND c.sendTo = :departmentId) " +
            "OR (c.receiverType = 'EMPLOYEE' AND c.sendTo = :employeeId)))) " +
            "GROUP BY t.announcement.id " +
            "ORDER BY t.announcement.createdAt DESC")
    Page<List<DataPreviewDTO>> getMainPreviews(@Param("companyId") Long companyId,
                                         @Param("departmentId") Long departmentId,
                                         @Param("employeeId") Long employeeId, Pageable pageable);


    @Query("SELECT new com.echo.acknowledgehub.dto.DataPreviewDTO(t.announcement.id, t.announcement.title) " +
            "FROM Target t " +
            "LEFT JOIN CustomTargetGroupEntity c ON c.customTargetGroup.id = t.sendTo " +
            "WHERE (t.announcement.employee.role = 'HR' OR t.announcement.employee.role = 'HR_ASSISTANCE') " +
            "AND ((t.receiverType = 'COMPANY' AND t.sendTo = :companyId) " +
            "OR (t.receiverType = 'DEPARTMENT' AND t.sendTo = :departmentId) " +
            "OR (t.receiverType = 'EMPLOYEE' AND t.sendTo = :employeeId) " +
            "OR (t.receiverType = 'CUSTOM' AND " +
            "((c.receiverType = 'COMPANY' AND c.sendTo = :companyId) " +
            "OR (c.receiverType = 'DEPARTMENT' AND c.sendTo = :departmentId) " +
            "OR (c.receiverType = 'EMPLOYEE' AND c.sendTo = :employeeId)))) " +
            "GROUP BY t.announcement.id " +
            "ORDER BY t.announcement.createdAt DESC")
    Page<List<DataPreviewDTO>> getSubPreviews(@Param("companyId") Long companyId,
                                              @Param("departmentId") Long departmentId,
                                              @Param("employeeId") Long employeeId,
                                              Pageable pageable);

    @Query("SELECT new com.echo.acknowledgehub.dto.AnnouncementsForShowing( " +
            "a.id, a.title, a.contentType, a.pdfLink," +
            "a.category.name, a.employee.name, a.createdAt,a.channel) " +
            "FROM Announcement a " +
            "WHERE a.id=:announcementId")
    AnnouncementsForShowing getAnnouncementById(@Param("announcementId") Long announcementId);

    @Query("SELECT new com.echo.acknowledgehub.dto.AnnouncementsShowInDashboard(a.id,a.title,c.name,e.name,e.role,a.createdAt)" +
            "FROM Announcement a JOIN a.category c JOIN a.employee e")
    List<AnnouncementsShowInDashboard> getAllAnnouncementsForDashboard();

    @Query("SELECT a.employee.id FROM Announcement a WHERE a.id=:announcementId")
    Long getCreator(@Param("announcementId") Long announcementId);

    @Query("SELECT t.announcement.id " +
            "FROM Target t " +
            "LEFT JOIN CustomTargetGroupEntity c ON c.customTargetGroup.id = t.sendTo " +
            "WHERE ((t.receiverType = 'COMPANY' AND t.sendTo = :companyId) " +
            "OR (t.receiverType = 'DEPARTMENT' AND t.sendTo = :departmentId) " +
            "OR (t.receiverType = 'EMPLOYEE' AND t.sendTo = :id) " +
            "OR (t.receiverType = 'CUSTOM' AND " +
            "((c.receiverType = 'COMPANY' AND c.sendTo = :companyId) " +
            "OR (c.receiverType = 'DEPARTMENT' AND c.sendTo = :departmentId) " +
            "OR (c.receiverType = 'EMPLOYEE' AND c.sendTo = :id)))) "
    )
    List<Long> canAccess(@Param("companyId") Long companyId, @Param("departmentId") Long departmentId, @Param("id") Long id);

    @Query("SELECT a.id FROM Announcement a JOIN Target t ON t.announcement = a " +
            "JOIN Employee e ON a.employee = e " +
            "WHERE e.id = :employeeId AND t.receiverType = 'COMPANY' AND t.sendTo = e.company.id AND a.status = 'UPLOADED' " +
            "GROUP BY a.id HAVING COUNT(t.id) = 1")
    List<Long> findAnnouncementIdsByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT COUNT(DISTINCT a.id) FROM Announcement a " +
            "JOIN Target t ON t.announcement = a " +
            "JOIN Employee e ON a.employee = e " +
            "WHERE e.id = :employeeId AND t.receiverType = 'COMPANY' AND t.sendTo = e.company.id AND a.status = 'UPLOADED'")
    int getAnnouncementCountByCompanyAndEmployee(@Param("employeeId") Long employeeId);



    @Query("SELECT e.id FROM Employee e WHERE e.role IN (:roles) AND e.company.id = :companyId")
    List<Long> findEmployeeIdsByRolesAndCompanyId(@Param("roles") List<EmployeeRole> roles, @Param("companyId") Long companyId);

    @Query("SELECT new com.echo.acknowledgehub.dto.ScheduleList(a.id, a.title, a.createdAt, a.contentType, e.role) " +
            "FROM Announcement a " +
            "JOIN a.employee e " +
            "WHERE a.status = :status AND e.id IN :employeeIds")
    List<ScheduleList> getScheduleListByEmployeeIds(@Param("status") AnnouncementStatus status, @Param("employeeIds") List<Long> employeeIds);

    @Query("SELECT a FROM Announcement a ORDER BY a.createdAt DESC")
    List<Announcement> findAllAnnouncements();

}