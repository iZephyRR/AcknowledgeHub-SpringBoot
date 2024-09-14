package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.CommentDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.Comment;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.service.AnnouncementService;
import com.echo.acknowledgehub.service.CommentService;
import com.echo.acknowledgehub.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@RequestMapping("${app.api.base-url}")
public class CommentController {

    private final CommentService COMMENT_SERVICE;
    private final ModelMapper MODEL_MAPPER;

    @PostMapping("/add")
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(COMMENT_SERVICE.addComment(commentDTO));
    }
}
