package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.constant.AnnouncementCategoryStatus;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnouncementCategoryRepository extends JpaRepository<AnnouncementCategory,Long> {
    @Modifying
    @Query("UPDATE AnnouncementCategory ac SET ac.status = :status WHERE ac.id = :id")
    int softDeleteById(@Param("id") Long id, @Param("status") AnnouncementCategoryStatus status);

    @Query("select ac from AnnouncementCategory ac order by ac.id DESC")
    List<AnnouncementCategory> getAllCategories();

    @Query("select ac from AnnouncementCategory ac where ac.status = :status")
    List<AnnouncementCategory> getActiveCategories(@Param("status") AnnouncementCategoryStatus status);
}
