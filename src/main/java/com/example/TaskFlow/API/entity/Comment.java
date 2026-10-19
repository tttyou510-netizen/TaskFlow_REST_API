package com.example.TaskFlow.API.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Entity
@Table(name="comment")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Comment {
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
private long id;
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private LocalDate  date;
    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "task_id")
private Task task;
}
