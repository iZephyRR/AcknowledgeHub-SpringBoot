package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.ReceiverType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "target", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"announcement_id", "receiver_type", "receiver_id"})
})
public class Target {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Enumerated(EnumType.STRING)
  @Column(name = "receiver_type",nullable = false,columnDefinition = "ENUM('DEPARTMENT', 'COMPANY', 'EMPLOYEE', 'CUSTOM')")
  private ReceiverType receiverType;
  @Column(name = "receiver_id", nullable = false, columnDefinition = "BIGINT")
  private Long sendTo;

  @JsonIgnore
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "announcement_id" ,nullable = false)
  private Announcement announcement;


}
