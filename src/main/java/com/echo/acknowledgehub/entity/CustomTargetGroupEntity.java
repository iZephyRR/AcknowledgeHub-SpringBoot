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
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "receiver_id", nullable = false, columnDefinition = "BIGINT")
  private Long sendTo;
  @Enumerated(EnumType.STRING)
  @Column(name = "receiver_type", nullable = false, columnDefinition = "ENUM('DEPARTMENT', 'COMPANY', 'EMPLOYEE', 'CUSTOM')")
  private ReceiverType receiverType;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "custom_group_id", nullable = false)
  private CustomTargetGroup customTargetGroup;
}
