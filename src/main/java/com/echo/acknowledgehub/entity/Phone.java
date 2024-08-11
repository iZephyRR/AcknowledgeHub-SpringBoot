package com.echo.acknowledgehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "phone")
public class Phone {
    @Id
    @Column(name = "phone",unique = true,nullable = false, columnDefinition = "VARCHAR(15)")
    private String phone;
    @Column(name = "count",nullable = false, columnDefinition = "TINYINT")
    private Byte count;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(nullable = false,name = "employee_id")
    private Employee employee;
}
