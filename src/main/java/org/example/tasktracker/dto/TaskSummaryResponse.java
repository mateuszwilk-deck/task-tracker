package org.example.tasktracker.dto;

public class TaskSummaryResponse {
    private final int total;
    private final int todo;
    private final int inProgress;
    private final int done;

    public TaskSummaryResponse(int total, int todo, int inProgress, int done) {
        this.total = total;
        this.todo = todo;
        this.inProgress = inProgress;
        this.done = done;
    }

    public int getTotal() {
        return total;
    }

    public int getTodo() {
        return todo;
    }

    public int getInProgress() {
        return inProgress;
    }

    public int getDone() {
        return done;
    }
}
