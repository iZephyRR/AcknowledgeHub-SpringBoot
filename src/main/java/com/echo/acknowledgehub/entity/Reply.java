package com.echo.acknowledgehub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reply")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(100)")
    private String title;

    @OneToOne(cascade =  CascadeType.MERGE)
    @JoinColumn(name = "comment_id" ,nullable = false)
    private Comment comment;
}
