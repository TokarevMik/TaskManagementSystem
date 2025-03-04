package org.example.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.model.Comment;
import org.example.model.Task;
import org.example.repository.CommentRepository;
import org.example.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Comment addCommentToTask(UUID taskId, Comment comment) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        comment.setTask(task);
         return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByTaskId(UUID taskId) {
        return commentRepository.findByTaskId(taskId);
    }

}
