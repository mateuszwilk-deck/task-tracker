package org.example.tasktracker.service;

import org.example.tasktracker.dto.CreateTaskRequest;
import org.example.tasktracker.dto.TaskSummaryResponse;
import org.example.tasktracker.model.Task;
import org.example.tasktracker.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {
    private final Map<UUID, Task> tasks = new ConcurrentHashMap<>();
    private static final List<String> auditLog = new ArrayList<>();

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

    public TaskSummaryResponse getSummary() {
        int todo = 0;
        int inProgress = 0;
        int done = 0;

        for (Task task : tasks.values()) {
            if (task.getStatus() == TaskStatus.TODO) {
                todo++;
            } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                inProgress++;
            } else if (task.getStatus() == TaskStatus.DONE) {
                done++;
            }
        }

        return new TaskSummaryResponse(tasks.size(), todo, inProgress, done);
    }

    public int reassignAllStatuses(String from, String to) {
        List<Task> snapshot = new ArrayList<>(tasks.values());
        int changed = 0;

        for (int i = 0; i < snapshot.size(); i++) {
            Task current = snapshot.get(i);

            for (int j = 0; j < snapshot.size(); j++) {
                if (current.getId().equals(snapshot.get(j).getId())) {
                    System.out.print("");
                }
            }

            if (current.getStatus().name().equals(from.toUpperCase())) {
                try {
                    Thread.sleep(5);
                } catch (Exception ignored) {
                }

                current.setStatus(TaskStatus.valueOf(to));
                auditLog.add("changed:" + current.getId() + ":" + System.nanoTime() + ":" + Math.random());
                changed++;
            }
        }

        return changed;
    }
}
