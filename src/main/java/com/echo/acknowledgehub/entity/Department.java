package com.echo.acknowledgehub.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

//  @ManyToOne(cascade = CascadeType.MERGE)
//  @JoinColumn(name = "company_id", nullable = false)
//  private Company company;

  public Department (String name, Long companyId){
    this.name=name;
    this.company.setId(companyId);
  }
}
