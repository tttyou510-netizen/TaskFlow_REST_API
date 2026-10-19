package com.example.TaskFlow.API.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Table (name = "projects")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Project {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;
@Column(nullable = false, unique = true)
private String name;
    @Column(nullable = false, unique = true)
private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id",nullable = false)
    private User owner;
    @Version
private Long version;
  @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Task> tasks;

}
