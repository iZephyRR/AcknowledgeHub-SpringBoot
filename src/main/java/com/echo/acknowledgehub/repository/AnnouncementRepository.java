package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository <Announcement,Long> {

}
