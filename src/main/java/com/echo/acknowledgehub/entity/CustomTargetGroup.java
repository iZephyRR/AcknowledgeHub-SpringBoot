package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "custom_target_group")
public class CustomTargetGroup {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(45)")
  private String title;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "hr_id", nullable = false)
  private Employee employee;
}
