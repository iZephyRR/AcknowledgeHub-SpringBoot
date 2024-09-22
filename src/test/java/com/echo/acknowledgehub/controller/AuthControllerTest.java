package com.echo.acknowledgehub.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.bean.SystemDataBean;
import com.echo.acknowledgehub.dto.ChangePasswordDTO;
import com.echo.acknowledgehub.dto.LoginDTO;
import com.echo.acknowledgehub.dto.StringResponseDTO;
import com.echo.acknowledgehub.service.EmployeeService;
import com.echo.acknowledgehub.util.EmailSender;
import com.echo.acknowledgehub.util.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.CompletableFuture;

class AuthControllerTest {
    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JWTService jwtService;

    @Mock
    private CheckingBean checkingBean;

    @Mock
    private EmailSender emailSender;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private SystemDataBean systemDataBean;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginDTO login = new LoginDTO();
        login.setEmail("test@example.com");
        login.setPassword("password");
        UserDetails userDetails = mock(UserDetails.class);
        String token = "jwtToken";

        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(jwtService.generateToken("test@example.com")).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(token));
    }

    @Test
    public void testChangePassword() throws Exception {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO();
        changePasswordDTO.setEmail("test@example.com");
        changePasswordDTO.setPassword("newPassword");
        when(employeeService.updatePassword(changePasswordDTO)).thenReturn(CompletableFuture.completedFuture(null));

        mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"oldPassword\", \"newPassword\":\"newPassword\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckEmail_Exists() throws Exception {
        String email = "test@example.com";
        when(employeeService.checkEmail(email)).thenReturn(CompletableFuture.completedFuture(true));

        mockMvc.perform(get("/auth/check-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"test@example.com\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    public void testGetDefaultPassword() throws Exception {
        when(systemDataBean.getDefaultPassword()).thenReturn("defaultPassword");

        mockMvc.perform(get("/user/default-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("defaultPassword"));
    }

    @Test
    public void testFindNameByEmail() throws Exception {
        String email = "test@example.com";
        String name = "Test User";
        StringResponseDTO responseDTO = new StringResponseDTO(name);
        when(employeeService.findNameByEmail(email)).thenReturn(CompletableFuture.completedFuture(responseDTO));
        mockMvc.perform(post("/auth/find-name-by-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"test@example.com\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(name));
    }
}