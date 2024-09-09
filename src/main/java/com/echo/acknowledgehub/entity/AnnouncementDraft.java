package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.ContentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "draft")
public class AnnouncementDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('EDITING', 'PENDING', 'APPROVED', 'DECLINED')")
    private AnnouncementStatus status;

    @Column(name = "fileUrl" ,nullable = false)
    private String fileUrl;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name="target", nullable = false, columnDefinition = "MEDIUMBLOB")
    private byte[] target;

    @Enumerated(EnumType.STRING)
    @Column(name = "contentType", nullable = false)
    private ContentType contentType;

    @Column(name = "draft_at", nullable = false, updatable = false)
    private LocalDate draftAt;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "hr_id" ,nullable = false)
    private Employee employee;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "category_id" ,nullable = false)
    private AnnouncementCategory category;

    @PrePersist
    protected void onCreate() {
        draftAt = LocalDate.now();
    }

}
