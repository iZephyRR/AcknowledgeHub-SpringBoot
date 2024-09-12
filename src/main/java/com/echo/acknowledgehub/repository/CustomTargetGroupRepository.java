package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.CustomTargetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomTargetGroupRepository extends JpaRepository<CustomTargetGroup,Long> {

    @Query("SELECT c FROM CustomTargetGroup c WHERE c.employee.id = :id")
    List<CustomTargetGroup> byHR(@Param("id") Long id);
}
