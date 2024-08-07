package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.CustomTargetGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomTargetGroupEntityRepository extends JpaRepository<CustomTargetGroupEntity,Long> {
}
