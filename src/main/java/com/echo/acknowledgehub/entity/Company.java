package com.echo.acknowledgehub.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

//  @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//  @JsonManagedReference
//  private List<Department> departments = new ArrayList<>();

  public Company(String name){
    this.name=name;
  }

  public Company(Long id){
    this.id=id;
  }
}
