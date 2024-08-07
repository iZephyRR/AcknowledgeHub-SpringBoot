package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "custom_target_group")
public class CustomTargetGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "employee", nullable = false)
  private Employee employee;
}
