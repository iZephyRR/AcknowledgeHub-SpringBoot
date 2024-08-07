package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.ReceiverType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "target")
public class Target {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private ReceiverType receiverType;

  private Long sendTo;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "announcement" ,nullable = false)
  private Announcement announcement;


}
