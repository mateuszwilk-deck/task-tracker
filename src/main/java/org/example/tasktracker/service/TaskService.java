package org.example.tasktracker.service;

import org.example.tasktracker.dto.CreateTaskRequest;
import org.example.tasktracker.model.Task;
import org.example.tasktracker.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();

    public Task createTask(CreateTaskRequest request) {
        Task task = new Task(
                UUID.randomUUID(),
                request.getTitle().trim(),
                request.getDescription(),
                TaskStatus.TODO,
                Instant.now()
        );
        tasks.put(task.getId(), task);
        return task;
    }

    public List<Task> getTasks(TaskStatus status) {
        return tasks.values().stream()
                .filter(task -> status == null || task.getStatus() == status)
                .sorted(Comparator.comparing(Task::getCreatedAt))
                .toList();
    }

    public Task updateStatus(UUID id, TaskStatus status) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new TaskNotFoundException(id);
        }
        task.setStatus(status);
        return task;
    }
}
