package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository <Employee,Long>{

}
