package com.echo.acknowledgehub.persistence.entity;

import com.echo.acknowledgehub.persistence.constant.AnnouncementStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "announcement")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('EDITING', 'PENDING', 'APPROVED', 'DECLINED')")
    private AnnouncementStatus status;
    @Column(name = "pdf_link", nullable = true,columnDefinition = "VARCHAR(125)")
    private String pdfLink;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "hr_id" ,nullable = false)
    private Employee employee;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id" ,nullable = false)
    private AnnouncementCategory category;

    @PrePersist
    private void prePersist(){
        if(createdAt!=null){
            createdAt = LocalDateTime.now();
        }
    }
}
