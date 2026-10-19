package com.example.TaskFlow.API.dto;

import com.example.TaskFlow.API.enums.TaskPriority;
import com.example.TaskFlow.API.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskRequestDto {
@Pattern(regexp = "^#[a-zA-Z0-9_-]{2,20}$", message = "Tag must start with '#' followed by 2 to 20 alphanumeric characters")
private String tag;

    private Long version;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    // Enums يجب مع @NotNull ليس @NotBlank
    @NotNull(message = "Status is required")
    private TaskStatus status;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    @NotNull(message = "Creation date is required")
    private LocalDateTime creationDate;

    // معرّف المشروع الذي تنتمي له المهمة
    @NotNull(message = "Project ID is required")
    private Integer projectId;
}
