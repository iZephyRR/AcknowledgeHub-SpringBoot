package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.dto.DepartmentDTO;
import com.echo.acknowledgehub.dto.DepartmentInfo;
import com.echo.acknowledgehub.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT new com.echo.acknowledgehub.dto.DepartmentDTO(d.id, d.name, d.company.name) FROM Department d")
    List<DepartmentDTO> findAllDTO();

    @Query("SELECT new com.echo.acknowledgehub.dto.DepartmentDTO(d.id, d.name, d.company.name) FROM Department d WHERE d.id= :id")
    DepartmentDTO findDTOByID(@Param("id") Long id);

    @Query("SELECT new com.echo.acknowledgehub.dto.DepartmentDTO(d.id, d.name, d.company.name) FROM Department d WHERE d.company.id= :companyId")
    List<DepartmentDTO> findAllDTOByCompany(@Param("companyId")Long companyId);

    @Query("SELECT d.name FROM Department d WHERE d.id = :id")
    String findDepartmentNameById(@Param("id") Long id);

    @Query("SELECT e.department FROM Employee e WHERE e.id=:employeeId")
    Department findByEmployeeId(@Param("employeeId") Long employeeId);

    @Query("SELECT d FROM Department d WHERE d.company.id=:id")
    List<Department> findByCompanyId(@Param("id") Long id);


}
