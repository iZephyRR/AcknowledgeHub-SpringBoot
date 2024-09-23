package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.CommentDTO;
import com.echo.acknowledgehub.dto.CommentResponseDTO;
import com.echo.acknowledgehub.dto.ReplyDTO;
import com.echo.acknowledgehub.dto.ReplyResponseDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.Comment;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.entity.Reply;
import com.echo.acknowledgehub.repository.CommentRepository;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import com.echo.acknowledgehub.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class CommentService {

    private static final Logger LOGGER = Logger.getLogger(CommentService.class.getName());
    private final AnnouncementService ANNOUNCEMENT_SERVICE;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final CommentRepository COMMENT_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;
    private final CheckingBean CHECKING_BEAN;
    private final FirebaseNotificationService FIREBASE_NOTIFICATION_SERVICE;
    private final ReplyRepository REPLY_REPOSITORY;

    public Comment addComment (CommentDTO commentDTO) {
        CompletableFuture<Announcement> announcementCompletableFuture = ANNOUNCEMENT_SERVICE.findById(commentDTO.getAnnouncementId())
                .thenApply(entity -> entity.orElseThrow(() -> new NoSuchElementException("Announcement not found")));
        CompletableFuture<Employee> employeeCompletableFuture = EMPLOYEE_SERVICE.findById(CHECKING_BEAN.getId())
                .thenApply(optionalEmployee -> optionalEmployee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
        Announcement announcement = announcementCompletableFuture.join();
        Employee employee = employeeCompletableFuture.join();
        String employeeName = employee.getName();
        String title = announcement.getTitle();
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setAnnouncement(announcementCompletableFuture.join());
        comment.setEmployee(employeeCompletableFuture.join());
        Comment savedComment = COMMENT_REPOSITORY.save(comment);
        FIREBASE_NOTIFICATION_SERVICE.saveComment(savedComment.getId(), announcement.getId(), employee.getId(), employeeName, savedComment.getContent());
        return savedComment;
    }

    public Reply replyToComment(ReplyDTO replyDTO) {
        LOGGER.info("reply to comment service");
        Comment originalComment = COMMENT_REPOSITORY.findById(replyDTO.getCommentId())
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        Employee replier = EMPLOYEE_REPOSITORY.findById(CHECKING_BEAN.getId())
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));
        Reply reply = new Reply();
        reply.setContent(replyDTO.getContent());
        reply.setComment(originalComment);
        reply.setEmployee(replier);
        Reply savedReply = REPLY_REPOSITORY.save(reply);
        FIREBASE_NOTIFICATION_SERVICE.saveReply(
                originalComment.getAnnouncement().getId(),
                savedReply.getId().toString(),
                originalComment.getId().toString(),
                replier.getName(),
                savedReply.getContent(),
                originalComment.getEmployee().getId().toString()   // Original Commenter's ID (Long)
        );
        return savedReply;
    }

    @Transactional
    public List<CommentResponseDTO> getByAnnouncement (Long id) {
        return COMMENT_REPOSITORY.getByAnnouncement(id);
    }

    @Transactional
    public List<ReplyResponseDTO> getByComment (Long id) {
        return COMMENT_REPOSITORY.getByComment(id);
    }

}