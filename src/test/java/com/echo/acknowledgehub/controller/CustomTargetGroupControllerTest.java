package com.echo.acknowledgehub.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.echo.acknowledgehub.dto.CustomTargetGroupDTO;
import com.echo.acknowledgehub.entity.CustomTargetGroup;
import com.echo.acknowledgehub.service.CustomTargetGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@WebMvcTest(CustomTargetGroupController.class)
public class CustomTargetGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomTargetGroupService customTargetGroupService;

    @InjectMocks
    private CustomTargetGroupController customTargetGroupController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() throws Exception {
        CustomTargetGroupDTO dto = new CustomTargetGroupDTO();
        // Set properties for dto as needed

        CustomTargetGroup savedGroup = new CustomTargetGroup();
        savedGroup.setTitle("Test Group"); // Example property

        when(customTargetGroupService.save(any())).thenReturn(CompletableFuture.completedFuture(savedGroup));

        mockMvc.perform(post("/custom-target")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Group\"}")) // Adjust based on your DTO structure
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(savedGroup.getTitle())); // Adjust based on your properties
    }

    @Test
    void testFindAll() throws Exception {
        when(customTargetGroupService.findAll()).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(new CustomTargetGroup())));

        mockMvc.perform(get("/custom-target"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))); // Adjust based on expected size
    }

    @Test
    void testFindById() throws Exception {
        Long id = 1L;
        CustomTargetGroup group = new CustomTargetGroup();
        group.setTitle("Test Group"); // Example property

        when(customTargetGroupService.findById(id)).thenReturn(CompletableFuture.completedFuture(Optional.of(group)));

        mockMvc.perform(get("/custom-target/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(group.getTitle())); // Adjust based on your properties
    }

    @Test
    void testByHR() throws Exception {
        when(customTargetGroupService.byHR()).thenReturn(CompletableFuture.completedFuture(Collections.singletonList(new CustomTargetGroup())));

        mockMvc.perform(get("/custom-target/by-hr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))); // Adjust based on expected size
    }

    @Test
    void testDeleteById() throws Exception {
        Long id = 1L;

        doNothing().when(customTargetGroupService).deleteById(id);

        mockMvc.perform(delete("/custom-target/{id}", id))
                .andExpect(status().isNoContent());
    }
}
