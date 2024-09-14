package com.echo.acknowledgehub.service;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.CommentDTO;
import com.echo.acknowledgehub.entity.Announcement;
import com.echo.acknowledgehub.entity.Comment;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class CommentService {

    private final AnnouncementService ANNOUNCEMENT_SERVICE;
    private final EmployeeService EMPLOYEE_SERVICE;
    private final CommentRepository COMMENT_REPOSITORY;
    private final ModelMapper MODEL_MAPPER;
    private final CheckingBean CHECKING_BEAN;

    public Comment addComment (CommentDTO commentDTO) {
        CompletableFuture<Announcement> announcementCompletableFuture = ANNOUNCEMENT_SERVICE.findById(commentDTO.getAnnouncementId())
                .thenApply(entity -> entity.orElseThrow(() -> new NoSuchElementException("Announcement not found")));
        CompletableFuture<Employee> employeeCompletableFuture = EMPLOYEE_SERVICE.findById(CHECKING_BEAN.getId())
                .thenApply(optionalEmployee -> optionalEmployee.orElseThrow(() -> new NoSuchElementException("Employee not found")));
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setAnnouncement(announcementCompletableFuture.join());
        comment.setEmployee(employeeCompletableFuture.join());
        return COMMENT_REPOSITORY.save(comment);
    }

}
