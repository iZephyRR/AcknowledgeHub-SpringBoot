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
  
    @Query("SELECT c.name FROM Company c WHERE c.id = :id")
    String findCompanyNameById(@Param("id") Long id);

    @Query("SELECT d.company FROM Department d WHERE d.id = :departmentId")
    Company getByDepartmentId(@Param("departmentId") Long id);

    @Query("SELECT e.company FROM Employee e WHERE e.id=:employeeId")
    Company findByEmployeeId(@Param("employeeId") Long employeeId);
}
