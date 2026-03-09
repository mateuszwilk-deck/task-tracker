package org.example.tasktracker.controller;

import jakarta.validation.Valid;
import org.example.tasktracker.dto.CreateTaskRequest;
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

    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) TaskStatus status) {
        return taskService.getTasks(status);
    }

    @PatchMapping("/{id}/status")
    public Task updateTaskStatus(@PathVariable UUID id, @Valid @RequestBody UpdateTaskStatusRequest request) {
        return taskService.updateStatus(id, request.getStatus());
    }
}
