package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.CustomTargetGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomTargetGroupRepository extends JpaRepository<CustomTargetGroup,Long> {
}
