package com.echo.acknowledgehub.persistence.repository;


import com.echo.acknowledgehub.persistence.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository <Announcement,Long> {

}
