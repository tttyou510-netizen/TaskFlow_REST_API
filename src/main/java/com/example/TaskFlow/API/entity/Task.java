package com.example.TaskFlow.API.entity;

import com.example.TaskFlow.API.enums.TaskPriority;
import com.example.TaskFlow.API.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.lang.model.element.Name;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="Tasks")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Task {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private  long Id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String  description;
    @Column(nullable = false)
    private LocalDateTime creationDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus Status;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
     private  User assignee;
     private String tag;
     @Version
     private Long version;
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name="project_id",nullable=false)
     private Project project;
     @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;
}
