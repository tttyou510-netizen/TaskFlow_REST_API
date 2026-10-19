package com.example.TaskFlow.API.service;

import com.example.TaskFlow.API.dto.TaskRequestDto;
import com.example.TaskFlow.API.dto.TaskResponseDto;
import com.example.TaskFlow.API.entity.Project;
import com.example.TaskFlow.API.entity.Task;
import com.example.TaskFlow.API.enums.TaskPriority;
import com.example.TaskFlow.API.enums.TaskStatus;
import com.example.TaskFlow.API.exception.ResourceNotFoundException;
import com.example.TaskFlow.API.notification.NotificationManager;
import com.example.TaskFlow.API.repository.ProjectRepository;
import com.example.TaskFlow.API.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private NotificationManager notificationManager;

    @InjectMocks
    private TaskService taskService;

    private Project sampleProject;
    private Task sampleTask;
    private TaskRequestDto sampleDto;

    @BeforeEach
    void setUp() {
        sampleProject = new Project();
        sampleProject.setId(1);
        sampleProject.setName("Backend Project");

        sampleTask = new Task();
        sampleTask.setId(10L);
        sampleTask.setTitle("Initial Task");
        sampleTask.setDescription("Initial Description");
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setPriority(TaskPriority.MEDIUM);
        sampleTask.setCreationDate(LocalDateTime.now());
        sampleTask.setProject(sampleProject);
        sampleTask.setVersion(1L);
        sampleTask.setTag("#dev");

        sampleDto = new TaskRequestDto();
        sampleDto.setProjectId(1);
        sampleDto.setTitle("New Task");
        sampleDto.setDescription("New Description");
        sampleDto.setStatus(TaskStatus.IN_PROGRESS);
        sampleDto.setPriority(TaskPriority.HIGH);
        sampleDto.setCreationDate(LocalDateTime.now());
        sampleDto.setVersion(1L);
        sampleDto.setTag("#dev");
    }

    @Test
    @DisplayName("Should create task successfully and send notification")
    void createTask_Success() {
        // Arrange
        when(projectRepository.findById(1)).thenReturn(Optional.of(sampleProject));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(100L);
            return task;
        });

        // Act
        TaskResponseDto response = taskService.createTask(sampleDto);

        // Assert
        assertNotNull(response);
        assertEquals("New Task", response.getTitle());
        assertEquals("Backend Project", response.getProjectName());
        assertEquals("#dev", response.getTag());

        // Verify notification was dispatched
        verify(notificationManager, times(1))
                .notify(eq("EMAIL"), anyString(), anyString(), anyString());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when project does not exist")
    void createTask_ProjectNotFound_ThrowsException() {
        // Arrange
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(sampleDto));
        verify(taskRepository, never()).save(any(Task.class));
        verify(notificationManager, never()).notify(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should update task successfully when versions match")
    void updateTask_Success() {
        // Arrange
        when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        sampleDto.setTitle("Updated Title");
        sampleDto.setVersion(1L);

        // Act
        TaskResponseDto response = taskService.updateTask(10L, sampleDto);

        // Assert
        assertNotNull(response);
        assertEquals("Updated Title", sampleTask.getTitle());
        verify(taskRepository, times(1)).save(sampleTask);
    }

    @Test
    @DisplayName("Should throw ObjectOptimisticLockingFailureException when versions conflict (Concurrent Edits)")
    void updateTask_OptimisticLockingConflict_ThrowsException() {
        // Arrange: Task in DB has version 2, but user DTO brings version 1
        sampleTask.setVersion(2L);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));

        sampleDto.setVersion(1L);

        // Act & Assert
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> {
            taskService.updateTask(10L, sampleDto);
        });

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task successfully when task exists")
    void deleteTask_Success() {
        // Arrange
        when(taskRepository.existsById(10L)).thenReturn(true);

        // Act
        taskService.deleteTask(10L);

        // Assert
        verify(taskRepository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent task")
    void deleteTask_NotFound_ThrowsException() {
        // Arrange
        when(taskRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.deleteTask(99L));
        verify(taskRepository, never()).deleteById(anyLong());
    }
}
