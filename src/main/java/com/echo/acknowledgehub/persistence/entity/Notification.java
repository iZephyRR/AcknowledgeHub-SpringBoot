package com.echo.acknowledgehub.persistence.entity;

import com.echo.acknowledgehub.persistence.constant.NotificationStatus;
import com.echo.acknowledgehub.persistence.constant.NotificationType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {
  @Column(name = "notice_at", columnDefinition = "TIMESTAMP")
  private LocalDateTime noticeAt;
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "ENUM('SEND', 'NOTED', 'DELETED') DEFAULT 'SEND'")
  private NotificationStatus status;
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, columnDefinition = "ENUM('REQUESTED', 'APPROVED', 'DECLINED', 'RECEIVED')")
  private NotificationType type;

  @Id
  @JsonIgnore
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;

  @Id
  @JsonIgnore
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "announcement_id", nullable = false)
  private Announcement announcement;
}
