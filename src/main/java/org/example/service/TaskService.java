package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.model.Comment;
import org.example.model.Status;
import org.example.model.Task;
import org.example.model.TaskPriority;
import org.example.repository.CommentRepository;
import org.example.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TaskService {
    private TaskRepository taskRepository;
    private CommentRepository commentRepository;

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getTasksByAuthor(UUID authorId) {
        return taskRepository.findByAuthorId(authorId);
    }

    public List<Task> getTasksByExecutor(UUID executorId) {
        return taskRepository.findByExecutorId(executorId);
    }

    public Task updateTaskStatus(UUID taskId, Status status) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public Task updateTaskPriority(UUID taskId, TaskPriority priority) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        task.setTaskPriority(priority);
        return taskRepository.save(task);
    }

    public Comment addCommentToTask(UUID taskId, Comment comment) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        comment.setTask(task);
        return commentRepository.save(comment);
    }
}
