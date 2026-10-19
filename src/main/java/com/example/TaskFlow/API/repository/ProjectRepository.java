package com.example.TaskFlow.API.repository;

import com.example.TaskFlow.API.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project,  Integer> {
}
