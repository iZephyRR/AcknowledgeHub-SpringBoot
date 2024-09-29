package com.echo.acknowledgehub.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.service.CustomTargetGroupEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomTargetGroupEntityController.class)
public class CustomTargetGroupEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomTargetGroupEntityService CUSTOM_TARGET_GROUP_ENTITY_SERVICE;

    @InjectMocks
    private CustomTargetGroupEntityController customTargetGroupEntityController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindTargetNameByTypeAndId() throws Exception {
        // Arrange
        ReceiverType receiverType = ReceiverType.COMPANY; // Replace with actual type
        Long receiverId = 1L;
        String expectedTargetName = "Example Target Name"; // Replace with expected result

        // Mock the service call
        when(CUSTOM_TARGET_GROUP_ENTITY_SERVICE.findTargetNameByTypeAndId(receiverType, receiverId))
                .thenReturn(expectedTargetName);

        // Act & Assert
        mockMvc.perform(get("/api/custom-group-entity")
                        .param("receiverType", receiverType.name())
                        .param("receiverId", String.valueOf(receiverId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(expectedTargetName)); // Assuming the response has a "value" field
    }

    // Add more tests for other scenarios, such as error handling, invalid parameters, etc.
}
