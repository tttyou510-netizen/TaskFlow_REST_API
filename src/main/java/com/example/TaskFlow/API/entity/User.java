package com.example.TaskFlow.API.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Entity
@Table(name="users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Email
    private String email;
    @Column(nullable = false)
    private String password;
      @OneToMany(mappedBy = "assignee")
    private List<Task> tasks;
       @OneToMany(mappedBy = "owner")
    private List<Project> projects;

}
