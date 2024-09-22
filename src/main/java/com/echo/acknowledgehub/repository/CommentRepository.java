package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.dto.CommentResponseDTO;
import com.echo.acknowledgehub.dto.ReplyResponseDTO;
import com.echo.acknowledgehub.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("SELECT new com.echo.acknowledgehub.dto.CommentResponseDTO(c.id, e.name, c.content, c.createdAt, e.photoLink) FROM Comment c " +
            "JOIN c.announcement a JOIN c.employee e WHERE a.id = :announcementId")
    List<CommentResponseDTO> getByAnnouncement(@Param("announcementId") Long announcementId);

    @Query("SELECT new com.echo.acknowledgehub.dto.ReplyResponseDTO(r.id, e.name, r.content, r.createdAt, e.photoLink) " +
            "FROM Reply r JOIN r.comment c JOIN r.employee e WHERE c.id = :commentId")
    List<ReplyResponseDTO> getByComment(@Param("commentId") Long commentId);
}
