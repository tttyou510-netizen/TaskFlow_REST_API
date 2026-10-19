package com.example.TaskFlow.API.service;

import com.example.TaskFlow.API.entity.Comment;
import com.example.TaskFlow.API.entity.Task;
import com.example.TaskFlow.API.exception.ResourceNotFoundException;
import com.example.TaskFlow.API.repository.CommentRepository;
import com.example.TaskFlow.API.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TaskRepository taskRepository;

    public Comment addComment(Long taskId, String content) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        Comment comment = new Comment();
        comment.setText(content);
        comment.setDate(LocalDate.now());
        comment.setTask(task);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTask(Long taskId) {
        return commentRepository.findByTaskId(taskId);
    }
}
