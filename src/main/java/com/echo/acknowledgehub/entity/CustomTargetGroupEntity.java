package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.ReceiverType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "custom_target_group_entity")
public class CustomTargetGroupEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long sendTo;
  private ReceiverType receiverType;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "custom_group", nullable = false)
  private CustomTargetGroup customTargetGroup;
}
