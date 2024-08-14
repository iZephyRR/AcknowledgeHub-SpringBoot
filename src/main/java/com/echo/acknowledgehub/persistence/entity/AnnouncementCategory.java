package com.echo.acknowledgehub.persistence.entity;

import com.echo.acknowledgehub.persistence.constant.AnnouncementCategoryStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "announcement_category")
public class AnnouncementCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "name", unique = true, nullable = false, columnDefinition = "VARCHAR(20)")
  private String name;
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, columnDefinition = "ENUM('ACTIVE', 'SOFT_DELETE')")
  private AnnouncementCategoryStatus status;

  @PrePersist
  private void prePersist(){
    this.status=AnnouncementCategoryStatus.ACTIVE;
  }
}
