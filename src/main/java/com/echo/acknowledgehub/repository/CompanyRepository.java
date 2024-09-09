package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    @Query("SELECT new com.echo.acknowledgehub.dto.CompanyDTO(c.id, c.name) FROM Company c WHERE c.id= :id")
    CompanyDTO findDTOByID(@Param("id") Long id);

    @Query("SELECT new com.echo.acknowledgehub.dto.CompanyDTO(c.id, c.name) FROM Company c")
    List<CompanyDTO> findAllDTO();
}
