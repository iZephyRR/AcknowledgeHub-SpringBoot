package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    private Long id;
    private String name;
    private String companyName;
    private List<Employee> employees;

    public DepartmentDTO(Long id, String name, String companyName){
        this.id=id;
        this.name=name;
        this.companyName=companyName;
    }
}
