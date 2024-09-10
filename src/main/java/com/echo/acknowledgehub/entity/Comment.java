package com.echo.acknowledgehub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "announcement_id" ,nullable = false)
    private Announcement announcement;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_id" ,nullable = false)
    private Employee employee;

}
