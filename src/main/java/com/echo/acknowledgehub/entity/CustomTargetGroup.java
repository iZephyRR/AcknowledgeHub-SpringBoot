package com.echo.acknowledgehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "hr_id", nullable = false)
  private Employee employee;

  @OneToMany(mappedBy = "customTargetGroup", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JsonManagedReference
  private List<CustomTargetGroupEntity> customTargetGroupEntities = new ArrayList<>();
}
