package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "department")
@Data
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "name", unique = true, nullable = false, columnDefinition = "VARCHAR(45)")
  private String name;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;
}
