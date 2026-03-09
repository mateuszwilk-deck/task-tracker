package org.example.tasktracker.dto;

import jakarta.validation.constraints.NotNull;
import org.example.tasktracker.model.TaskStatus;

public class UpdateTaskStatusRequest {
    @NotNull
    private TaskStatus status;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
