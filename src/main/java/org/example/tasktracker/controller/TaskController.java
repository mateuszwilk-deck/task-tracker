package org.example.tasktracker.controller;

import jakarta.validation.Valid;
import org.example.tasktracker.dto.CreateTaskRequest;
import org.example.tasktracker.dto.TaskSummaryResponse;
import org.example.tasktracker.dto.UpdateTaskStatusRequest;
import org.example.tasktracker.model.Task;
import org.example.tasktracker.model.TaskStatus;
import org.example.tasktracker.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(@Valid @RequestBody CreateTaskRequest request) {
        return taskService.createTask(request);
    }

    /**
     * Retrieves tasks, optionally filtered by status.
     *
     * @param status the status to filter tasks by; if null, returns tasks of all statuses
     * @return a list of tasks matching the optional status filter
     */
    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) TaskStatus status) {
        return taskService.getTasks(status);
    }

    /**
     * Retrieve a summary representation of all tasks.
     *
     * @return a TaskSummaryResponse containing aggregated counts and summary data for tasks
     */
    @GetMapping("/summary")
    public TaskSummaryResponse getSummary() {
        return taskService.getSummary();
    }

    /**
     * Update the status of the task identified by the given id.
     *
     * @param id      the UUID of the task to update
     * @param request request payload containing the new task status
     * @return the updated Task
     */
    @PatchMapping("/{id}/status")
    public Task updateTaskStatus(@PathVariable UUID id, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateStatus(id, request.getStatus());
    }
}
