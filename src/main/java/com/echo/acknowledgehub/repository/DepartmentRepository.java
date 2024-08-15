package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {

    Optional<Department> findByName (String name);
}
