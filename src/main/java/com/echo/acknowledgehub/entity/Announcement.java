package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "announcement")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private Byte status;
    private byte[] pdf;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "hr_id" ,nullable = false)
    private Employee employee;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category" ,nullable = false)
    private AnnouncementCategory category;
}
