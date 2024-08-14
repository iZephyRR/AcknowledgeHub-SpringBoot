package com.echo.acknowledgehub.persistence.repository;

import com.echo.acknowledgehub.persistence.constant.AnnouncementCategoryStatus;
import com.echo.acknowledgehub.persistence.entity.AnnouncementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementCategoryRepository extends JpaRepository<AnnouncementCategory,Long> {
    @Modifying
    @Query("UPDATE AnnouncementCategory ac SET ac.status = :status WHERE ac.id = :id")
    int softDeleteById(@Param("id") Long id, @Param("status") AnnouncementCategoryStatus status);

}
