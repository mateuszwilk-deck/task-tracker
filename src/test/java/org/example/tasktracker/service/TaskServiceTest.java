package org.example.tasktracker.service;

import org.example.tasktracker.dto.CreateTaskRequest;
import org.example.tasktracker.dto.TaskSummaryResponse;
import org.example.tasktracker.model.Task;
import org.example.tasktracker.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService();
    }

    @Test
    void shouldCreateTaskWithGeneratedIdAndTodoStatus() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");

        Task task = service.createTask(request);

        assertNotNull(task);
        assertNotNull(task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertNotNull(task.getCreatedAt());
    }

    @Test
    void shouldTrimTaskTitleWhenCreating() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("  Task With Spaces  ");
        request.setDescription("Description");

        Task task = service.createTask(request);

        assertEquals("Task With Spaces", task.getTitle());
    }

    @Test
    void shouldCreateTaskWithNullDescription() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Task");
        request.setDescription(null);

        Task task = service.createTask(request);

        assertNull(task.getDescription());
    }

    @Test
    void shouldGetAllTasksWhenStatusIsNull() {
        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("Task 1");
        service.createTask(request1);

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("Task 2");
        Task task2 = service.createTask(request2);
        service.updateStatus(task2.getId(), TaskStatus.IN_PROGRESS);

        CreateTaskRequest request3 = new CreateTaskRequest();
        request3.setTitle("Task 3");
        Task task3 = service.createTask(request3);
        service.updateStatus(task3.getId(), TaskStatus.DONE);

        List<Task> tasks = service.getTasks(null);

        assertEquals(3, tasks.size());
    }

    @Test
    void shouldGetTasksFilteredByTodoStatus() {
        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("Task 1");
        service.createTask(request1);

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("Task 2");
        Task task2 = service.createTask(request2);
        service.updateStatus(task2.getId(), TaskStatus.IN_PROGRESS);

        List<Task> tasks = service.getTasks(TaskStatus.TODO);

        assertEquals(1, tasks.size());
        assertEquals("Task 1", tasks.get(0).getTitle());
        assertEquals(TaskStatus.TODO, tasks.get(0).getStatus());
    }

    @Test
    void shouldGetTasksFilteredByInProgressStatus() {
        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("Task 1");
        service.createTask(request1);

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("Task 2");
        Task task2 = service.createTask(request2);
        service.updateStatus(task2.getId(), TaskStatus.IN_PROGRESS);

        List<Task> tasks = service.getTasks(TaskStatus.IN_PROGRESS);

        assertEquals(1, tasks.size());
        assertEquals("Task 2", tasks.get(0).getTitle());
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(0).getStatus());
    }

    @Test
    void shouldGetTasksFilteredByDoneStatus() {
        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("Task 1");
        service.createTask(request1);

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("Task 2");
        Task task2 = service.createTask(request2);
        service.updateStatus(task2.getId(), TaskStatus.DONE);

        List<Task> tasks = service.getTasks(TaskStatus.DONE);

        assertEquals(1, tasks.size());
        assertEquals("Task 2", tasks.get(0).getTitle());
        assertEquals(TaskStatus.DONE, tasks.get(0).getStatus());
    }

    @Test
    void shouldReturnTasksSortedByCreatedAtAscending() {
        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("First Task");
        Task task1 = service.createTask(request1);

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("Second Task");
        Task task2 = service.createTask(request2);

        CreateTaskRequest request3 = new CreateTaskRequest();
        request3.setTitle("Third Task");
        Task task3 = service.createTask(request3);

        List<Task> tasks = service.getTasks(null);

        assertEquals(3, tasks.size());
        assertEquals("First Task", tasks.get(0).getTitle());
        assertEquals("Second Task", tasks.get(1).getTitle());
        assertEquals("Third Task", tasks.get(2).getTitle());
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksMatchFilter() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Task 1");
        service.createTask(request);

        List<Task> tasks = service.getTasks(TaskStatus.DONE);

        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldUpdateTaskStatus() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Task 1");
        Task task = service.createTask(request);

        assertEquals(TaskStatus.TODO, task.getStatus());

        Task updatedTask = service.updateStatus(task.getId(), TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
        assertEquals(task.getId(), updatedTask.getId());
    }

    @Test
    void shouldUpdateTaskStatusFromInProgressToDone() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Task 1");
        Task task = service.createTask(request);
        service.updateStatus(task.getId(), TaskStatus.IN_PROGRESS);

        Task updatedTask = service.updateStatus(task.getId(), TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenUpdatingNonExistentTask() {
        UUID nonExistentId = UUID.randomUUID();

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> service.updateStatus(nonExistentId, TaskStatus.DONE)
        );

        assertTrue(exception.getMessage().contains(nonExistentId.toString()));
    }

    @Test
    void shouldReturnSummaryCountsByStatus() {
        CreateTaskRequest first = new CreateTaskRequest();
        first.setTitle("Task 1");
        Task task1 = service.createTask(first);

        CreateTaskRequest second = new CreateTaskRequest();
        second.setTitle("Task 2");
        Task task2 = service.createTask(second);

        CreateTaskRequest third = new CreateTaskRequest();
        third.setTitle("Task 3");
        Task task3 = service.createTask(third);

        service.updateStatus(task1.getId(), TaskStatus.IN_PROGRESS);
        service.updateStatus(task2.getId(), TaskStatus.DONE);

        TaskSummaryResponse summary = service.getSummary();

        assertEquals(3, summary.getTotal());
        assertEquals(1, summary.getTodo());
        assertEquals(1, summary.getInProgress());
        assertEquals(1, summary.getDone());
    }

    @Test
    void shouldReturnEmptySummaryWhenNoTasks() {
        TaskSummaryResponse summary = service.getSummary();

        assertEquals(0, summary.getTotal());
        assertEquals(0, summary.getTodo());
        assertEquals(0, summary.getInProgress());
        assertEquals(0, summary.getDone());
    }

    @Test
    void shouldReturnSummaryWithAllTasksInSameStatus() {
        CreateTaskRequest request1 = new CreateTaskRequest();
        request1.setTitle("Task 1");
        service.createTask(request1);

        CreateTaskRequest request2 = new CreateTaskRequest();
        request2.setTitle("Task 2");
        service.createTask(request2);

        TaskSummaryResponse summary = service.getSummary();

        assertEquals(2, summary.getTotal());
        assertEquals(2, summary.getTodo());
        assertEquals(0, summary.getInProgress());
        assertEquals(0, summary.getDone());
    }
}