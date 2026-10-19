package com.example.TaskFlow.API.repository;

import com.example.TaskFlow.API.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task,Long> {
}
