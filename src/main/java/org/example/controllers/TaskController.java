package org.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.model.Comment;
import org.example.model.Status;
import org.example.model.Task;
import org.example.service.CommentService;
import org.example.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Task", description = "API for managing tasks")
@RequiredArgsConstructor
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;

    @Operation(summary = "Create task", description = "Creates a new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid task data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to create tasks")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }
    @Operation(summary = "Get tasks by author", description = "Retrieves all tasks created by a specific author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to view tasks")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/author/{authorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getTasksByAuthor(@PathVariable UUID authorId) {
        return ResponseEntity.ok(taskService.getTasksByAuthor(authorId));
    }
    @Operation(summary = "Get tasks by executor", description = "Retrieves all tasks assigned to a specific executor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to view tasks")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/executor/{executorId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Task>> getTasksByExecutor(@PathVariable UUID executorId) {
        return ResponseEntity.ok(taskService.getTasksByExecutor(executorId));
    }
    @Operation(summary = "Update task status", description = "Updates the status of a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid task ID or status"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to update tasks"),
            @ApiResponse(responseCode = "404", description = "Not Found - Task not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{taskId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable UUID taskId, @RequestBody Status status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, status));
    }
    @Operation(summary = "Add comment to task", description = "Adds a new comment to a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid task ID or comment data"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to add comments"),
            @ApiResponse(responseCode = "404", description = "Not Found - Task not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{taskId}/comments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Comment> addCommentToTask(@PathVariable UUID taskId, @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.addCommentToTask(taskId, comment));
    }
    @Operation(summary = "Get comments by task", description = "Retrieves all comments for a specific task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    })
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId));
    }
}
