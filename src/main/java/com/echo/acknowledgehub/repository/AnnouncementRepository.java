package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.IsSchedule;
import com.echo.acknowledgehub.constant.SelectAll;
import com.echo.acknowledgehub.dto.AnnouncementDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface AnnouncementRepository extends JpaRepository <Announcement,Long> {

    @Query("SELECT a FROM Announcement a WHERE a.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<Announcement> findAllByDateBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT a FROM Announcement a WHERE a.status = :status AND a.createdAt <= :now")
    List<Announcement> findByStatusAndScheduledTime( @Param("status") AnnouncementStatus status, @Param("now") LocalDateTime now);

    @Query("SELECT a.id, a.createdAt, a.status, a.title, a.contentType, c.name as categoryName, e.name as creator,e.role as role,a.pdfLink FROM Announcement a " +
            "JOIN a.employee e JOIN a.category c order by a.createdAt DESC")
    List<Object[]> getAllAnnouncements();

    @Query("SELECT new com.echo.acknowledgehub.dto.AnnouncementDTO(a.title, a.createdAt, a.status, a.category.name, a.employee.name) FROM Announcement a WHERE a.employee.company.id = :id ORDER BY a.createdAt DESC")
    List<AnnouncementDTO> getByCompany(@Param("id") Long id);

    @Query("SELECT a FROM Announcement a WHERE a.status = :status AND a.isSchedule = :isSchedule AND a.createdAt <= :now")
    List<Announcement> findByStatusAndScheduledTime(
            @Param("status") AnnouncementStatus status,
            @Param("isSchedule") IsSchedule isSchedule,
            @Param("now") LocalDateTime now
    );

    @Query("select id from Announcement where selectAll=:selectAll")
    List<Long> getSelectedAllAnnouncements(@Param("selectAll") SelectAll selectAll);

    @Query("select count(a) from Announcement a where a.selectAll=:selectAll")
    int getSelectAllCountAnnouncements(@Param("selectAll") SelectAll selectAll);

}