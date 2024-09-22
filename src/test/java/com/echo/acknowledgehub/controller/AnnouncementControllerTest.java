package com.echo.acknowledgehub.controller;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.AnnouncementStatus;
import com.echo.acknowledgehub.constant.ContentType;
import com.echo.acknowledgehub.constant.ReceiverType;
import com.echo.acknowledgehub.dto.*;
import com.echo.acknowledgehub.entity.*;
import com.echo.acknowledgehub.service.*;
import com.echo.acknowledgehub.util.EmailSender;
import com.echo.acknowledgehub.util.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class AnnouncementControllerTest {

    @Mock
    private AnnouncementService announcementService;

    @Mock
    private JWTService jwtService;

    @Mock
    private CheckingBean checkingBean;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private CompanyService companyService;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private AnnouncementCategoryService announcementCategoryService;

    @Mock
    private TelegramService telegramService;

    @Mock
    private TargetService targetService;

    @Mock
    private DraftService draftService;

    @Mock
    private EmailSender emailSender;

    @Mock
    private CustomTargetGroupEntityService customTargetGroupEntityService;

    @InjectMocks
    private AnnouncementController announcementController;

    private Map<Long, SaveTargetsForSchedule> targetStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        targetStorage = new HashMap<>();
        targetStorage.put(1L, new SaveTargetsForSchedule());
    }

    @Test
    void testCheckPendingAnnouncements() throws ExecutionException, InterruptedException, IOException {
        // Arrange
        List<Announcement> pendingAnnouncements = new ArrayList<>();
        Announcement mockAnnouncement = new Announcement();
        mockAnnouncement.setId(1L);
        mockAnnouncement.setContentType(ContentType.PDF);
        mockAnnouncement.setTitle("Test Announcement");
        mockAnnouncement.setPdfLink("someUrl");
        pendingAnnouncements.add(mockAnnouncement);

        when(announcementService.findPendingAnnouncementsScheduledForNow(any(LocalDateTime.class)))
                .thenReturn(pendingAnnouncements);

        SaveTargetsForSchedule saveTargetsForSchedule = new SaveTargetsForSchedule();
        saveTargetsForSchedule.setIsEmailSelected("emailSelected");
        saveTargetsForSchedule.setTargets(Collections.emptyList());
        announcementController.targetStorage.put(mockAnnouncement.getId(), saveTargetsForSchedule);

        // Act
        announcementController.checkPendingAnnouncements();

        // Assert
        verify(announcementService, times(1)).save(mockAnnouncement);
        verify(telegramService, times(1)).sendToTelegram(anyList(), any(), any(), anyLong(), anyString(), anyString(), anyString());
        verify(emailSender, times(1)).sendEmail(any(EmailDTO.class));

        assertEquals(AnnouncementStatus.UPLOADED, mockAnnouncement.getStatus());
    }

    @Test
    void testCreateAnnouncement() throws IOException, ExecutionException, InterruptedException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "Test content".getBytes());
        AnnouncementDTO announcementDTO = new AnnouncementDTO();
        announcementDTO.setTitle("Test Title");
        announcementDTO.setScheduleOption("now");
        announcementDTO.setFile(file);

        Announcement mockAnnouncement = new Announcement();
        mockAnnouncement.setId(1L);
        when(modelMapper.map(any(AnnouncementDTO.class), any(Class.class))).thenReturn(mockAnnouncement);

        when(announcementService.save(any(Announcement.class))).thenReturn(mockAnnouncement);

        List<TargetDTO> targetDTOs = new ArrayList<>();
        TargetDTO targetDTO = new TargetDTO();
        targetDTO.setReceiverType(ReceiverType.COMPANY);
        targetDTO.setSendTo(1L);
        targetDTOs.add(targetDTO);

        ObjectMapper objectMapper = new ObjectMapper();
        String targetJson = objectMapper.writeValueAsString(targetDTOs);
        announcementDTO.setTarget(targetJson);

        when(companyService.existsById(1L)).thenReturn(true);

        // Act
        announcementController.createAnnouncement(announcementDTO);

        // Assert
        verify(announcementService, times(1)).save(any(Announcement.class));
        verify(telegramService, times(1)).sendToTelegram(anyList(), any(), any(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    void testValidateTargets() {
        // Arrange
        List<TargetDTO> targetDTOs = new ArrayList<>();
        TargetDTO targetDTO = new TargetDTO();
        targetDTO.setReceiverType(ReceiverType.COMPANY);
        targetDTO.setSendTo(1L);
        targetDTOs.add(targetDTO);

        when(companyService.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> announcementController.validateTargets(targetDTOs));
    }

    @Test
    void testValidateTargetsThrowsException() {
        // Arrange
        List<TargetDTO> targetDTOs = new ArrayList<>();
        TargetDTO targetDTO = new TargetDTO();
        targetDTO.setReceiverType(ReceiverType.COMPANY);
        targetDTO.setSendTo(1L);
        targetDTOs.add(targetDTO);

        when(companyService.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> announcementController.validateTargets(targetDTOs));
    }

    @Test
    void testHandleEmails() {
        // Arrange
        List<CustomTargetGroupEntity> groupEntities = new ArrayList<>();
        CustomTargetGroupEntity entity = new CustomTargetGroupEntity();
        entity.setReceiverType(ReceiverType.COMPANY);
        entity.setSendTo(1L);
        groupEntities.add(entity);

// Mock the service to return a CompletableFuture<Set<String>>
        when(employeeService.getEmailsByCompanyId(1L))
                .thenReturn(CompletableFuture.completedFuture(Collections.singleton("email@test.com"))
                        .thenApply(set -> new ArrayList<>(set)));

        // Act
        Set<String> emails = announcementController.handleEmails(groupEntities);

        // Assert
        assertEquals(1, emails.size());
        assertTrue(emails.contains("email@test.com"));
    }

    @Test
    void testHandleChatIds() {
        // Arrange
        List<CustomTargetGroupEntity> groupEntities = new ArrayList<>();
        CustomTargetGroupEntity entity = new CustomTargetGroupEntity();
        entity.setReceiverType(ReceiverType.COMPANY);
        entity.setSendTo(1L);
        groupEntities.add(entity);

        when(employeeService.getAllChatIdByCompanyId(1L)).thenReturn((List<Long>) Collections.singleton(12345L));

        // Act
        Set<Long> chatIds = announcementController.handleChatIds(groupEntities);

        // Assert
        assertEquals(1, chatIds.size());
        assertTrue(chatIds.contains(12345L));
    }

    @Test
    void testUploadDraftSuccess() throws IOException {
        // Prepare data
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "Test Data".getBytes());
        AnnouncementDraftDTO draftDTO = new AnnouncementDraftDTO();
        draftDTO.setFile(file);
        draftDTO.setCategoryId(1L);

        AnnouncementCategory category = new AnnouncementCategory();
        Employee employee = new Employee();
        AnnouncementDraft draft = new AnnouncementDraft();
        draft.setTitle("Test Title");

        // Mock services
        when(employeeService.findById(anyLong())).thenReturn(CompletableFuture.completedFuture(Optional.of(employee)));
        when(announcementCategoryService.findById(anyLong())).thenReturn(Optional.of(category));
        when(draftService.saveDraft(any(AnnouncementDraft.class))).thenReturn(draft);
        when(employeeService.findById(anyLong())).thenReturn(CompletableFuture.completedFuture(Optional.of(new Employee())));

        // Test the uploadDraft method
        ResponseEntity<AnnouncementDraft> response = announcementController.uploadDraft(draftDTO);

        // Assert the results
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Title", response.getBody().getTitle());
    }

    @Test
    void testUploadDraftFailure() throws IOException {
        // Prepare data
        MultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "Test Data".getBytes());
        AnnouncementDraftDTO draftDTO = new AnnouncementDraftDTO();
        draftDTO.setFile(file);
        draftDTO.setCategoryId(1L);

        // Mock services
        when(employeeService.findById(anyLong())).thenReturn(CompletableFuture.completedFuture(Optional.empty()));

        // Test the uploadDraft method
        assertThrows(NoSuchElementException.class, () -> announcementController.uploadDraft(draftDTO));
    }

    @Test
    void testGetDraftById() {
        // Prepare data
        AnnouncementDraft draft = new AnnouncementDraft();
        draft.setId(1L);
        draft.setCategory(new AnnouncementCategory());
        draft.setTarget("encodedTarget".getBytes());
        when(draftService.getById(anyLong())).thenReturn(draft);

        // Test the method
        ResponseEntity<AnnouncementDraftDTO> response = announcementController.getDraftById(1L);

        // Assert the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testGetAll() {
        // Prepare data
        List<AnnouncementsShowInDashboard> announcements = Collections.emptyList();
        when(announcementService.getAllAnnouncementsForDashboard()).thenReturn(announcements);

        // Test the method
        ResponseEntity<List<AnnouncementsShowInDashboard>> response = announcementController.getAll();

        // Assert the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetAnnouncementsForAugToOct2024() {
        // Prepare data
        Map<String, List<Announcement>> announcementsByMonth = Collections.emptyMap();
        when(announcementService.getAnnouncementsForAugToOct2024()).thenReturn(announcementsByMonth);

        // Test the method
        ResponseEntity<Map<String, List<Announcement>>> response = announcementController.getAnnouncementsForAugToOct2024();

        // Assert the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testDeleteDraft() throws IOException {
        // Prepare data
        AnnouncementDraft draft = new AnnouncementDraft();
        draft.setFileUrl("testPath");
        when(draftService.getById(anyLong())).thenReturn(draft);

        // Test the method
        ResponseEntity<StringResponseDTO> response = announcementController.deleteDraft(1L);

        // Assert the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Draft Deleted Successfully", response.getBody().STRING_RESPONSE());
    }

    @Test
    void testUpdateNow() throws IOException, ExecutionException, InterruptedException {
        // Prepare data
        Announcement announcement = new Announcement();
        announcement.setId(1L);
        announcement.setContentType(ContentType.PDF);

        SaveTargetsForSchedule targetsForSchedule = new SaveTargetsForSchedule();
        when(announcementService.updateTimeAndStatus(anyLong())).thenReturn(announcement);

        when(targetStorage.get(anyLong())).thenReturn(targetsForSchedule);

        // Test the method
        ResponseEntity<StringResponseDTO> response = announcementController.updateNow(1L);

        // Assert the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Upload Success", response.getBody().STRING_RESPONSE());
    }

    @Test
    void testGetScheduleList() {
        // Prepare data
        List<ScheduleList> scheduleList = Collections.emptyList();
        when(announcementService.getScheduleList()).thenReturn(scheduleList);

        // Test the method
        List<ScheduleList> result = announcementController.getScheduleList();

        // Assert the result
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}