package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.dto.CommentResponseDTO;
import com.echo.acknowledgehub.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("SELECT new com.echo.acknowledgehub.dto.CommentResponseDTO(c.id, e.name, c.content, c.createdAt) FROM Comment c " +
            "JOIN c.announcement a JOIN c.employee e WHERE a.id = :announcementId")
    List<CommentResponseDTO> getByAnnouncement(Long announcementId);
}
