package org.example.repository;

import org.example.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByAuthorId(UUID authorId);
    List<Task> findByExecutorId(UUID executorId);
}
