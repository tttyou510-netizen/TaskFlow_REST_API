package com.example.TaskFlow.API.mapper;

import com.example.TaskFlow.API.dto.TaskRequestDto;
import com.example.TaskFlow.API.dto.TaskResponseDto;
import com.example.TaskFlow.API.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskResponseDto toDto(Task task);
    Task toEntity(TaskRequestDto dto);
    void updateEntityFromDto(TaskRequestDto dto, @MappingTarget Task task);
}
