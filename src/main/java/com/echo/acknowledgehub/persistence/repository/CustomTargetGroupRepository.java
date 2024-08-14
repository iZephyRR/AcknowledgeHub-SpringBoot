package com.echo.acknowledgehub.persistence.repository;

import com.echo.acknowledgehub.persistence.entity.CustomTargetGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomTargetGroupRepository extends JpaRepository<CustomTargetGroup,Long> {
}
