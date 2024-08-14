package com.echo.acknowledgehub.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "company")
public class Company {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "name", unique = true, nullable = false, columnDefinition = "VARCHAR(45)")
  private String name;

  public Company(String name){
    this.name=name;
  }
}
