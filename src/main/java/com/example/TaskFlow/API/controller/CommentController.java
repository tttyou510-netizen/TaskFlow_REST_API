package com.example.TaskFlow.API.controller;

import com.example.TaskFlow.API.entity.Comment;
import com.example.TaskFlow.API.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable Long taskId,
                                              @RequestBody Map<String, String> body) {
        String content = body.get("content");
        return ResponseEntity.status(201).body(commentService.addComment(taskId, content));
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }
}
