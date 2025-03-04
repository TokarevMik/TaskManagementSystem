import org.example.model.Status;
import org.example.model.Task;
import org.example.model.TaskPriority;
import org.example.repository.TaskRepository;
import org.example.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    @Test
    void testCreateTask() {
        Task task = new Task(UUID.randomUUID(), "Test Task", "Description", Status.PENDING, TaskPriority.MIDDLE, null, null, null);
        when(taskRepository.save(task)).thenReturn(task);
        Task savedTask = taskService.createTask(task);
        assertEquals("Test Task", savedTask.getName());
    }
}