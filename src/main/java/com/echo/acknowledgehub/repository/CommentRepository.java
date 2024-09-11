package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
