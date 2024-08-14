package com.echo.acknowledgehub.persistence.repository;

import com.echo.acknowledgehub.persistence.entity.Target;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TargetRepository extends JpaRepository<Target,Long> {
}
