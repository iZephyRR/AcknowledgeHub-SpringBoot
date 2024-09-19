package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.CustomTargetGroup;
import com.echo.acknowledgehub.entity.CustomTargetGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomTargetGroupEntityRepository extends JpaRepository<CustomTargetGroupEntity,Long> {

    @Query("SELECT c FROM CustomTargetGroupEntity c WHERE c.customTargetGroup = :customTargetGroup")
    List<CustomTargetGroupEntity> findByCustomTargetGroup(CustomTargetGroup customTargetGroup);

}
