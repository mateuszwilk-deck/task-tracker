package org.example.tasktracker.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskSummaryResponseTest {

    @Test
    void shouldCreateTaskSummaryResponseWithAllFields() {
        TaskSummaryResponse response = new TaskSummaryResponse(10, 5, 3, 2);

        assertEquals(10, response.getTotal());
        assertEquals(5, response.getTodo());
        assertEquals(3, response.getInProgress());
        assertEquals(2, response.getDone());
    }

    @Test
    void shouldCreateTaskSummaryResponseWithZeroValues() {
        TaskSummaryResponse response = new TaskSummaryResponse(0, 0, 0, 0);

        assertEquals(0, response.getTotal());
        assertEquals(0, response.getTodo());
        assertEquals(0, response.getInProgress());
        assertEquals(0, response.getDone());
    }

    @Test
    void shouldCreateTaskSummaryResponseWithAllTasksInTodo() {
        TaskSummaryResponse response = new TaskSummaryResponse(5, 5, 0, 0);

        assertEquals(5, response.getTotal());
        assertEquals(5, response.getTodo());
        assertEquals(0, response.getInProgress());
        assertEquals(0, response.getDone());
    }

    @Test
    void shouldCreateTaskSummaryResponseWithAllTasksInProgress() {
        TaskSummaryResponse response = new TaskSummaryResponse(3, 0, 3, 0);

        assertEquals(3, response.getTotal());
        assertEquals(0, response.getTodo());
        assertEquals(3, response.getInProgress());
        assertEquals(0, response.getDone());
    }

    @Test
    void shouldCreateTaskSummaryResponseWithAllTasksDone() {
        TaskSummaryResponse response = new TaskSummaryResponse(7, 0, 0, 7);

        assertEquals(7, response.getTotal());
        assertEquals(0, response.getTodo());
        assertEquals(0, response.getInProgress());
        assertEquals(7, response.getDone());
    }

    @Test
    void shouldMaintainImmutabilityOfFields() {
        TaskSummaryResponse response = new TaskSummaryResponse(10, 5, 3, 2);

        int firstTotal = response.getTotal();
        int secondTotal = response.getTotal();

        assertEquals(firstTotal, secondTotal);
        assertEquals(10, response.getTotal());
    }

    @Test
    void shouldHandleLargeNumbers() {
        TaskSummaryResponse response = new TaskSummaryResponse(1000, 500, 300, 200);

        assertEquals(1000, response.getTotal());
        assertEquals(500, response.getTodo());
        assertEquals(300, response.getInProgress());
        assertEquals(200, response.getDone());
    }
}