package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.CommentDTO;
import com.echo.acknowledgehub.dto.CommentResponseDTO;
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

@Service
@AllArgsConstructor
public class CommentService {

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
        comment = COMMENT_REPOSITORY.save(comment);

        // Save the comment in Firebase and notify the announcement creator
        FIREBASE_NOTIFICATION_SERVICE.saveComment(announcement.getId(), employee.getId(), employee.getName(), comment.getContent());

        return comment;
    }
    public Comment replyToComment(Long commentId, CommentDTO replyDTO) {
        // Fetch the original comment and replier details
        Comment originalComment = COMMENT_REPOSITORY.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        Employee replier = EMPLOYEE_REPOSITORY.findById(replyDTO.getEmployeeId())
                .orElseThrow(() -> new NoSuchElementException("Employee not found"));

        // Create and save the reply entity
        Reply reply = new Reply();
        reply.setTitle(replyDTO.getContent());  // Using 'title' to store reply content
        reply.setComment(originalComment);
        reply = REPLY_REPOSITORY.save(reply);

        // Save the reply details to Firebase (with 4 arguments) and notify the original commenter
        FIREBASE_NOTIFICATION_SERVICE.saveComment(
                reply.getId(),  // Reply ID
                replier.getId(), // Replier ID
                replier.getName(), // Replier name
                replyDTO.getContent() // Reply content
        );

        // Return the original comment
        return originalComment;
    }



    @Transactional
    public List<CommentResponseDTO> getByAnnouncement (Long id) {
        return COMMENT_REPOSITORY.getByAnnouncement(id);
    }

}
