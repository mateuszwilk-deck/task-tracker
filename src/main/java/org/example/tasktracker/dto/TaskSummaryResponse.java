package org.example.tasktracker.dto;

public class TaskSummaryResponse {
    private final int total;
    private final int todo;
    private final int inProgress;
    private final int done;

    /**
     * Create a TaskSummaryResponse containing counts of tasks by status.
     *
     * @param total      total number of tasks
     * @param todo       number of tasks in the TODO state
     * @param inProgress number of tasks in the IN_PROGRESS state
     * @param done       number of tasks in the DONE state
     */
    public TaskSummaryResponse(int total, int todo, int inProgress, int done) {
        this.total = total;
        this.todo = todo;
        this.inProgress = inProgress;
        this.done = done;
    }

    /**
     * Retrieves the total number of tasks across all statuses.
     *
     * @return the total number of tasks
     */
    public int getTotal() {
        return total;
    }

    /**
     * Provides the count of tasks in the TODO state.
     *
     * @return the number of tasks in the TODO state.
     */
    public int getTodo() {
        return todo;
    }

    /**
     * Retrieves the number of tasks in the IN_PROGRESS state.
     *
     * @return the number of tasks in the IN_PROGRESS state
     */
    public int getInProgress() {
        return inProgress;
    }

    /**
     * Gets the number of tasks in the DONE state.
     *
     * @return the count of tasks in the DONE state
     */
    public int getDone() {
        return done;
    }
}
