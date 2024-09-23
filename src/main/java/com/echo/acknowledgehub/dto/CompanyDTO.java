package com.echo.acknowledgehub.dto;

import com.echo.acknowledgehub.entity.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    private String name;
    private List<DepartmentDTO> departments;

    public CompanyDTO(Long id,  String name){
        this.id=id;
        this.name=name;
    }
}
