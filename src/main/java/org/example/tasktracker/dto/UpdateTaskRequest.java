package org.example.tasktracker.dto;

import java.util.List;

public record UpdateTaskRequest(
        String title,
        String description,
        Boolean completed,
        Integer priority,
        Integer estimateHours,
        List<String> tags,
        String overrideOwnerId
) {
}
