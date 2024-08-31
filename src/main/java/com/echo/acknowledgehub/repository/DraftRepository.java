package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.entity.AnnouncementDraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DraftRepository extends JpaRepository<AnnouncementDraft, Long> {

    @Query("SELECT d.id, d.title, d.fileUrl, c.name, d.filename, d.contentType,  d.draftAt, c.id FROM AnnouncementDraft d \n" +
            "JOIN AnnouncementCategory c ON c.id = d.category.id WHERE d.employee.id = :id AND d.status = :status")
    List<Object[]> getDrafts(@Param("id") Long id,@Param("status") AnnouncementStatus status);
}
