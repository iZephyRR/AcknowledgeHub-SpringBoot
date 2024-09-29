package com.echo.acknowledgehub.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.echo.acknowledgehub.dto.CommentDTO;
import com.echo.acknowledgehub.dto.CommentResponseDTO;
import com.echo.acknowledgehub.dto.ReplyDTO;
import com.echo.acknowledgehub.dto.ReplyResponseDTO;
import com.echo.acknowledgehub.entity.Comment;
import com.echo.acknowledgehub.entity.Reply;
import com.echo.acknowledgehub.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private CommentDTO commentDTO;
    private ReplyDTO replyDTO;
    private Long announcementId = 1L;
    private Long commentId = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        commentDTO = new CommentDTO(); // Set properties for commentDTO
        replyDTO = new ReplyDTO(); // Set properties for replyDTO
    }

    @Test
    public void addComment_ShouldReturnComment() throws Exception {
        Comment comment = new Comment(); // Create and set properties for the Comment object
        when(commentService.addComment(any(CommentDTO.class))).thenReturn(comment);

        mockMvc.perform(post("/comments/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Sample comment\"}")) // Adjust as per your CommentDTO structure
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Sample comment")); // Adjust according to your response structure
    }

    @Test
    public void getById_ShouldReturnListOfComments() throws Exception {
        // Sample data for CommentResponseDTO
        Long id = 1L;
        String author = "Jane Doe";
        String content = "This is a comment.";
        LocalDateTime createdAt = LocalDateTime.now();
        byte[] photoLink = new byte[]{0, 1, 2}; // Sample byte array; replace with actual image data if needed.

        // Create a CommentResponseDTO instance
        CommentResponseDTO commentResponseDTO = new CommentResponseDTO(id, author, content, createdAt, photoLink);

        // Create a list containing the comment
        List<CommentResponseDTO> comments = Collections.singletonList(commentResponseDTO);

        // Mock the service call
        when(commentService.getByAnnouncement(announcementId)).thenReturn(comments);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/comments/getById/{id}", announcementId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].author").value(author))
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].createdAt").exists()) // Assuming this is serialized properly
                .andExpect(jsonPath("$[0].photoLink").exists()); // Adjust this based on how the byte array is serialized
    }


    @Test
    public void replyToComment_ShouldReturnReply() throws Exception {
        Reply reply = new Reply(); // Create and set properties for the Reply object
        when(commentService.replyToComment(any(ReplyDTO.class))).thenReturn(reply);

        mockMvc.perform(post("/comments/replyToComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"Sample reply\"}")) // Adjust as per your ReplyDTO structure
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Sample reply")); // Adjust according to your response structure
    }

    @Test
    public void getReply_ShouldReturnListOfReplies() throws Exception {
        // Sample data for ReplyResponseDTO
        Long id = 1L;
        String replierName = "John Doe";
        String replyContent = "This is a reply.";
        LocalDateTime replyCreatedAt = LocalDateTime.now();
        byte[] replierPhotoLink = new byte[]{0, 1, 2}; // Sample byte array; replace with actual image data if needed.

        // Create a ReplyResponseDTO instance
        ReplyResponseDTO replyResponseDTO = new ReplyResponseDTO(id, replierName, replyContent, replyCreatedAt, replierPhotoLink);

        // Create a list containing the reply
        List<ReplyResponseDTO> replies = Collections.singletonList(replyResponseDTO);

        // Mock the service call
        when(commentService.getByComment(commentId)).thenReturn(replies);

        // Perform the GET request and assert the response
        mockMvc.perform(get("/comments/getReplyBy/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].replierName").value(replierName))
                .andExpect(jsonPath("$[0].replyContent").value(replyContent))
                .andExpect(jsonPath("$[0].replyCreatedAt").exists()) // Assuming this is serialized properly
                .andExpect(jsonPath("$[0].replierPhotoLink").exists()); // Adjust this based on how the byte array is serialized
    }

}
