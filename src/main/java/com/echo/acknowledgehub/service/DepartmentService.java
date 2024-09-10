package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.dto.DepartmentDTO;
import com.echo.acknowledgehub.dto.UserDTO;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.DataNotFoundException;
import com.echo.acknowledgehub.repository.DepartmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class DepartmentService {
    private static final Logger LOGGER = Logger.getLogger(DepartmentService.class.getName());
    private final DepartmentRepository DEPARTMENT_REPOSITORY;

    @Async
    public CompletableFuture<Optional<Department>> findById(Long id){
        return CompletableFuture.completedFuture(DEPARTMENT_REPOSITORY.findById(id));
    }

    @Async
    public CompletableFuture<DepartmentDTO> findDTOById(Long id){
        DepartmentDTO departmentDTO=DEPARTMENT_REPOSITORY.findDTOByID(id);
        if(departmentDTO!=null){
            return CompletableFuture.completedFuture(departmentDTO);
        }else {
            throw new DataNotFoundException("Cannot find department.");
        }
    }

    @Async
    public CompletableFuture<Department> save(Department department){
        return CompletableFuture.completedFuture(DEPARTMENT_REPOSITORY.save(department));
    }

    @Async
    public void delete(Long id){
        DEPARTMENT_REPOSITORY.deleteById(id);
    }

    public List<Department> getAll() {
        return DEPARTMENT_REPOSITORY.findAll();
    }
    public List<DepartmentDTO> getAllDTO() {
        return DEPARTMENT_REPOSITORY.findAllDTO();
    }
    public List<DepartmentDTO> getAllDTOByCompany(Long companyId) {
        return DEPARTMENT_REPOSITORY.findAllDTOByCompany(companyId);
    }

    public boolean existsById(Long sendTo) {
        return DEPARTMENT_REPOSITORY.existsById(sendTo);
    }
}
