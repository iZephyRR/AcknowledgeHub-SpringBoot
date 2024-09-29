package com.echo.acknowledgehub.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.dto.CompanyDTO;
import com.echo.acknowledgehub.dto.HRDTO;
import com.echo.acknowledgehub.entity.AnnouncementCategory;
import com.echo.acknowledgehub.entity.Company;
import com.echo.acknowledgehub.entity.Department;
import com.echo.acknowledgehub.service.AnnouncementCategoryService;
import com.echo.acknowledgehub.service.CompanyService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CompanyService companyService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private AnnouncementCategoryService announcementCategoryService;

    @Mock
    private CheckingBean checkingBean;

    @InjectMocks
    private CompanyController companyController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(companyController).build();
    }

//    @Test
//    public void testGetCompany() throws Exception {
//        Long companyId = 1L;
//        Company company = new Company();
//        company.setId(companyId);
//        when(companyService.findById(companyId)).thenReturn(CompletableFuture.completedFuture(Optional.of(company)));
//
//        mockMvc.perform(get("/user/get-company/{id}", companyId))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(companyId));
//
//        verify(companyService, times(1)).findById(companyId);
//    }

    @Test
    public void testGetCompanyDTO() throws Exception {
        Long companyId = 1L;
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(companyId);
        when(companyService.findDTOById(companyId)).thenReturn(CompletableFuture.completedFuture(companyDTO));

        mockMvc.perform(get("/user/get-company-dto/{id}", companyId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(companyId));

        verify(companyService, times(1)).findDTOById(companyId);
    }

    @Test
    void testGetDepartments() {
        List<Department> departments = new ArrayList<>();
        departments.add(new Department());
        when(departmentService.getAll()).thenReturn(departments);

        List<Department> result = companyController.getDepartments();
        assertEquals(departments, result);
    }

    @Test
    void testGetCategories() {
        List<AnnouncementCategory> categories = new ArrayList<>();
        categories.add(new AnnouncementCategory());
        when(announcementCategoryService.getActiveCategories()).thenReturn(categories);

        List<AnnouncementCategory> result = companyController.getCategories();
        assertEquals(categories, result);
    }

//    @Test
//    public void testGetCompanies() throws Exception {
//        when(companyService.getAll()).thenReturn(Collections.singletonList(new Company()));
//
//        mockMvc.perform(get("/mr/get-companies"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//
//        verify(companyService, times(1)).getAll();
//    }

    @Test
    public void testGetCompanyByIdFound() throws Exception {
        Long companyId = 1L;
        when(checkingBean.getCompanyId()).thenReturn(companyId);
        Company company = new Company();
        company.setId(companyId);
        when(companyService.getCompanyById(companyId)).thenReturn(Optional.of(company));

        mockMvc.perform(get("/getCompanyById"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(companyId));

        verify(companyService, times(1)).getCompanyById(companyId);
    }

    @Test
    public void testGetCompanyByIdNotFound() throws Exception {
        Long companyId = 1L;
        when(checkingBean.getCompanyId()).thenReturn(companyId);
        when(companyService.getCompanyById(companyId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/getCompanyById"))
                .andExpect(status().isNotFound());

        verify(companyService, times(1)).getCompanyById(companyId);
    }

    @Test
    public void testSave() throws Exception {
        HRDTO hrdto = new HRDTO();
        when(companyService.saveHR(hrdto)).thenReturn(CompletableFuture.completedFuture(hrdto));

        mockMvc.perform(post("/mr/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ /* JSON representation of HRDTO */ }"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(companyService, times(1)).saveHR(hrdto);
    }
}
