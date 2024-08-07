package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.AnnouncementCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementCategoryRepository extends JpaRepository<AnnouncementCategory,Long> {

}
