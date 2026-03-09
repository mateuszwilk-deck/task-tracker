package org.example.tasktracker.service;

import org.example.tasktracker.dto.CreateTaskRequest;
import org.example.tasktracker.dto.TaskSummaryResponse;
import org.example.tasktracker.model.Task;
import org.example.tasktracker.model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskServiceTest {

    @Test
    void shouldReturnSummaryCountsByStatus() {
        TaskService service = new TaskService();

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
}
