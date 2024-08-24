package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository <Announcement,Long> {

    @Query("SELECT a FROM Announcement a WHERE a.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<Announcement> findAllByDateBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT a FROM Announcement a WHERE a.status = :status AND a.createdAt <= :now") //a.status = :status AND
    List<Announcement> findByStatusAndScheduledTime(@Param("status") AnnouncementStatus status, @Param("now") LocalDateTime now); //@Param("status") AnnouncementStatus status,

}