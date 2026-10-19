package com.example.TaskFlow.API.dto;

import com.example.TaskFlow.API.enums.TaskPriority;
import com.example.TaskFlow.API.enums.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime creationDate;
    private String projectName;   // اسم المشروع بدل ID لعرض أوضح للمستخدم
    private String tag;
    private Long version;
}
