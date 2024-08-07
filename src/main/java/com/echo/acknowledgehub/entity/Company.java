package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "company")
public class Company {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "name", unique = true, nullable = false, columnDefinition = "VARCHAR(45)")
  private String name;
}
