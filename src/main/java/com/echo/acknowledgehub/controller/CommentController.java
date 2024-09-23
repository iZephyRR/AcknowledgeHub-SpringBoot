package com.echo.acknowledgehub.controller;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.CommentDTO;
import com.echo.acknowledgehub.dto.CommentResponseDTO;
import com.echo.acknowledgehub.dto.ReplyDTO;
import com.echo.acknowledgehub.dto.ReplyResponseDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.Comment;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.Reply;
import com.echo.acknowledgehub.service.AnnouncementService;
import com.echo.acknowledgehub.service.CommentService;
import com.echo.acknowledgehub.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
@RequestMapping("${app.api.base-url}/comments")
public class CommentController {

    private final CommentService COMMENT_SERVICE;
    private final ModelMapper MODEL_MAPPER;

    @PostMapping(value = "/add")
    public ResponseEntity<Comment> addComment(@RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(COMMENT_SERVICE.addComment(commentDTO));
    }

    @GetMapping(value = "/getById/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentResponseDTO>> getById(@PathVariable("id") Long announcementId) {
        return ResponseEntity.ok(COMMENT_SERVICE.getByAnnouncement(announcementId));
    }

    @PostMapping(value = "/replyToComment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Reply> replyToComment(@RequestBody ReplyDTO replyDTO) {
        return ResponseEntity.ok(COMMENT_SERVICE.replyToComment(replyDTO));
    }

    @GetMapping(value = "/getReplyBy/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReplyResponseDTO>> getReply (@PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(COMMENT_SERVICE.getByComment(commentId));
    }

}
