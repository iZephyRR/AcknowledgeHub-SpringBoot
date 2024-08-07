package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "department")
@Data
public class Department {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "company", nullable = false)
  private Company company;
}
