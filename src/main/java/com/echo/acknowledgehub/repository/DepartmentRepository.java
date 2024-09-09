package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.dto.DepartmentDTO;
import com.echo.acknowledgehub.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT new com.echo.acknowledgehub.dto.DepartmentDTO(d.id, d.name, d.company.name) FROM Department d")
    List<DepartmentDTO> findAllDTO();

    @Query("SELECT new com.echo.acknowledgehub.dto.DepartmentDTO(d.id, d.name, d.company.name) FROM Department d WHERE d.id= :id")
    DepartmentDTO findDTOByID(@Param("id") Long id);

    @Query("SELECT new com.echo.acknowledgehub.dto.DepartmentDTO(d.id, d.name, d.company.name) FROM Department d WHERE d.company.id= :companyId")
    List<DepartmentDTO> findAllDTOByCompany(@Param("companyId")Long companyId);
}
