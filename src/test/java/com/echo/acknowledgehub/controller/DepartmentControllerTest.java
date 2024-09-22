package com.echo.acknowledgehub.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.DepartmentDTO;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.service.DepartmentService;
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private CheckingBean checkingBean;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController).build();
    }

    @Test
    public void testSave() throws Exception {
        Department department = new Department();
        // Set fields on department as necessary for the test

        when(departmentService.save(any())).thenReturn(CompletableFuture.completedFuture(department));

        mockMvc.perform(post("/api/v1/hrs/department")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ /* JSON representation of department */ }"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentService, times(1)).save(any());
    }

    @Test
    public void testFindAllDTOByCompanyId() throws Exception {
        Long companyId = 1L;
        List<DepartmentDTO> departmentDTOs = Collections.singletonList(new DepartmentDTO());

        when(departmentService.getAllDTOByCompany(companyId)).thenReturn(departmentDTOs);

        mockMvc.perform(get("/api/v1/hrs/department/by-company/{id}", companyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));

        verify(departmentService, times(1)).getAllDTOByCompany(companyId);
    }

    @Test
    public void testFindById() throws Exception {
        Long departmentId = 1L;
        DepartmentDTO departmentDTO = new DepartmentDTO();
        // Set fields on departmentDTO as necessary for the test

        when(departmentService.findDTOById(departmentId)).thenReturn(CompletableFuture.completedFuture(departmentDTO));

        mockMvc.perform(get("/api/v1/hrs/department/dto/{id}", departmentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(departmentService, times(1)).findDTOById(departmentId);
    }

    @Test
    public void testDelete() throws Exception {
        Long departmentId = 1L;

        doNothing().when(departmentService).delete(departmentId);

        mockMvc.perform(delete("/api/v1/hrs/department/{id}", departmentId))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).delete(departmentId);
    }
}
