package com.example.TaskFlow.API.service;

import com.example.TaskFlow.API.dto.TaskRequestDto;
import com.example.TaskFlow.API.dto.TaskResponseDto;
import com.example.TaskFlow.API.entity.Project;
import com.example.TaskFlow.API.entity.Task;
import com.example.TaskFlow.API.exception.ResourceNotFoundException;
import com.example.TaskFlow.API.notification.NotificationManager;
import com.example.TaskFlow.API.repository.ProjectRepository;
import com.example.TaskFlow.API.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private NotificationManager notificationManager;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto) {

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + dto.getProjectId()));
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setCreationDate(dto.getCreationDate());
        task.setTag(dto.getTag());
        task.setProject(project);
        Task saved = taskRepository.save(task);

        // إرسال إشعار بإنشاء المهمة عبر الـ Strategy
        notificationManager.notify("EMAIL", "team@taskflow.com", "Task Created", "New task created: " + saved.getTitle());

        return mapToResponseDto(saved);
    }

    public Page<TaskResponseDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(this::mapToResponseDto);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // التحقق من تضارب التعديلات المتزامنة (Optimistic Locking)
        if (dto.getVersion() != null && !task.getVersion().equals(dto.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Task.class, id);
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setCreationDate(dto.getCreationDate());
        if (dto.getTag() != null) {
            task.setTag(dto.getTag());
        }
        Task updated = taskRepository.save(task);
        return mapToResponseDto(updated);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private TaskResponseDto mapToResponseDto(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreationDate(task.getCreationDate());
        dto.setTag(task.getTag());
        dto.setVersion(task.getVersion());
        if (task.getProject() != null) {
            dto.setProjectName(task.getProject().getName());
        }
        return dto;
    }
}
