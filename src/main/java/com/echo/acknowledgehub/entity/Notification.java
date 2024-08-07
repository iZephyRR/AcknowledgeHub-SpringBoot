package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.NotificationStatus;
import com.echo.acknowledgehub.constant.NotificationType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification")
public class Notification {
  private LocalDateTime noticeAt;
  private NotificationStatus status;
  private NotificationType type;

  @Id
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "employee", nullable = false)
  private Employee employee;

  @Id
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "announcement", nullable = false)
  private Announcement announcement;
}
