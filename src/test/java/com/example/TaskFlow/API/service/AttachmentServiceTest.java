package com.example.TaskFlow.API.service;

import com.example.TaskFlow.API.entity.Attachment;
import com.example.TaskFlow.API.entity.Task;
import com.example.TaskFlow.API.exception.ResourceNotFoundException;
import com.example.TaskFlow.API.repository.AttachmentRepository;
import com.example.TaskFlow.API.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private AttachmentService attachmentService;

    @TempDir
    Path tempUploadDir;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        // Set temp directory for safe testing without polluting local filesystem
        ReflectionTestUtils.setField(attachmentService, "uploadDir", tempUploadDir.toString());
        ReflectionTestUtils.setField(attachmentService, "uploadPath", tempUploadDir);

        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Test Task");
    }

    @Test
    @DisplayName("Should upload attachment successfully when file and regex are valid")
    void uploadAttachment_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.pdf",
                "application/pdf",
                "Sample PDF content".getBytes()
        );

        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(invocation -> {
            Attachment a = invocation.getArgument(0);
            a.setId(100L);
            return a;
        });

        // Act
        Attachment result = attachmentService.uploadAttachment(1L, file);

        // Assert
        assertNotNull(result);
        assertEquals("report.pdf", result.getOriginalFileName());
        assertTrue(result.getStoredFileName().endsWith(".pdf"));
        assertEquals("application/pdf", result.getFileType());
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file has invalid extension (Regex fails)")
    void uploadAttachment_InvalidExtension_ThrowsException() {
        // Arrange: uploading an unauthorized .exe file
        MockMultipartFile maliciousFile = new MockMultipartFile(
                "file",
                "virus.exe",
                "application/octet-stream",
                "dummy content".getBytes()
        );

        // Act & Assert (Regex validation check)
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            attachmentService.uploadAttachment(1L, maliciousFile);
        });

        assertTrue(ex.getMessage().contains("Invalid file name or unsupported format"));
        verify(taskRepository, never()).findById(anyLong());
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file is empty")
    void uploadAttachment_EmptyFile_ThrowsException() {
        // Arrange: empty file
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            attachmentService.uploadAttachment(1L, emptyFile);
        });
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when task is not found")
    void uploadAttachment_TaskNotFound_ThrowsException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "image data".getBytes()
        );
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            attachmentService.uploadAttachment(99L, file);
        });
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    @DisplayName("Should return list of attachments for a given task")
    void getAttachmentsByTaskId_Success() {
        // Arrange
        Attachment a1 = new Attachment();
        a1.setId(1L);
        a1.setOriginalFileName("file1.pdf");

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(attachmentRepository.findByTaskId(1L)).thenReturn(List.of(a1));

        // Act
        List<Attachment> list = attachmentService.getAttachmentsByTaskId(1L);

        // Assert
        assertEquals(1, list.size());
        assertEquals("file1.pdf", list.get(0).getOriginalFileName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when attachment does not exist")
    void getAttachmentById_NotFound_ThrowsException() {
        // Arrange
        when(attachmentRepository.findById(404L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            attachmentService.getAttachmentById(404L);
        });
    }
}
