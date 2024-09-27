package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.*;
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
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('EDITING', 'PENDING', 'UPLOADED')")
    private AnnouncementStatus status;
    @Column(name = "pdf_link", nullable = true,columnDefinition = "VARCHAR(150)")
    private String pdfLink;
    @Enumerated(EnumType.STRING)
    @Column(name = "contentType", nullable = false)
    private ContentType contentType;
    @Enumerated(EnumType.STRING)
    @Column(name = "selectAll", nullable = false , columnDefinition = "ENUM('TRUE', 'FALSE')")
    private SelectAll selectAll;
    @Enumerated(EnumType.STRING)
    @Column(name ="isSchedule", nullable = false, columnDefinition = "ENUM('TRUE', 'FALSE')")
    private IsSchedule isSchedule;
    @Enumerated(EnumType.STRING)
    @Column(name ="channel", nullable = false , columnDefinition = "ENUM('TELEGRAM', 'BOTH')")
    private Channel channel;
    @Column(name = "deadline")
    private LocalDateTime deadline;

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
        if(createdAt == null){
            throw new IllegalArgumentException("CreatedAt must be set before saving the announcement.");
        }
    }
}
