package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company,Long> {

    Optional<Company> findByName(String name);

    @Query("SELECT c.name FROM Company c WHERE c.id = :id")
    String findCompanyNameById(@Param("id") Long id);
}
