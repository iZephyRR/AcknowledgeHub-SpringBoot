package com.echo.acknowledgehub.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "department")
@Data
@NoArgsConstructor
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(45)")
  private String name;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "company_id", nullable = false)
  @JsonBackReference
  private Company company;

  @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<Employee> employees = new ArrayList<>();

//  @ManyToOne(cascade = CascadeType.MERGE)
//  @JoinColumn(name = "company_id", nullable = false)
//  private Company company;

  public Department (String name, Long companyId){
    this.name=name;
    this.company.setId(companyId);
  }
}
