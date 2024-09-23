package com.echo.acknowledgehub.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.hasSize;

class AnnouncementCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AnnouncementCategoryService announcementCategoryService;

    @InjectMocks
    private AnnouncementCategoryController announcementCategoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(announcementCategoryController).build();
    }

    @Test
    public void testCreateCategory() throws Exception {
        AnnouncementCategory category = new AnnouncementCategory();
        category.setId(1L);
        // Set other properties of category as needed

        when(announcementCategoryService.save(any())).thenReturn(CompletableFuture.completedFuture(category));

        mockMvc.perform(post("/create-category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1}") // Adjust JSON based on your category structure
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(announcementCategoryService, times(1)).save(any());
    }

    @Test
    public void testGetAllCategories() throws Exception {
        AnnouncementCategory category1 = new AnnouncementCategory();
        category1.setId(1L);
        AnnouncementCategory category2 = new AnnouncementCategory();
        category2.setId(2L);
        List<AnnouncementCategory> categories = Arrays.asList(category1, category2);

        when(announcementCategoryService.getAllCategoriesDESC()).thenReturn(categories);

        mockMvc.perform(get("/get-all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(announcementCategoryService, times(1)).getAllCategoriesDESC();
    }

    @Test
    public void testSoftDeleteCategory() throws Exception {
        Long categoryId = 1L;
        when(announcementCategoryService.softDelete(categoryId)).thenReturn(CompletableFuture.completedFuture(1));

        mockMvc.perform(put("/disable/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(announcementCategoryService, times(1)).softDelete(categoryId);
    }

    @Test
    public void testSoftUndeleteCategory() throws Exception {
        Long categoryId = 1L;
        when(announcementCategoryService.softUndelete(categoryId)).thenReturn(CompletableFuture.completedFuture(1));

        mockMvc.perform(put("/enable/{id}", categoryId))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(announcementCategoryService, times(1)).softUndelete(categoryId);
    }
}