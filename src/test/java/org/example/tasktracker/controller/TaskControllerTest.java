package org.example.tasktracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.tasktracker.dto.CreateTaskRequest;
import org.example.tasktracker.dto.TaskSummaryResponse;
import org.example.tasktracker.dto.UpdateTaskStatusRequest;
import org.example.tasktracker.model.Task;
import org.example.tasktracker.model.TaskStatus;
import org.example.tasktracker.service.TaskNotFoundException;
import org.example.tasktracker.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setDescription("Task Description");

        Task createdTask = new Task(
                UUID.randomUUID(),
                "New Task",
                "Task Description",
                TaskStatus.TODO,
                Instant.now()
        );

        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(createdTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(createdTask.getId().toString()))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task Description"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("");
        request.setDescription("Description");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsNull() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle(null);
        request.setDescription("Description");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        Task task1 = new Task(UUID.randomUUID(), "Task 1", "Desc 1", TaskStatus.TODO, Instant.now());
        Task task2 = new Task(UUID.randomUUID(), "Task 2", "Desc 2", TaskStatus.IN_PROGRESS, Instant.now());
        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskService.getTasks(null)).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[0].status").value("TODO"))
                .andExpect(jsonPath("$[1].title").value("Task 2"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    void shouldGetTasksFilteredByTodoStatus() throws Exception {
        Task task = new Task(UUID.randomUUID(), "Task 1", "Desc 1", TaskStatus.TODO, Instant.now());
        List<Task> tasks = Collections.singletonList(task);

        when(taskService.getTasks(TaskStatus.TODO)).thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("TODO"));
    }

    @Test
    void shouldGetTasksFilteredByInProgressStatus() throws Exception {
        Task task = new Task(UUID.randomUUID(), "Task 1", "Desc 1", TaskStatus.IN_PROGRESS, Instant.now());
        List<Task> tasks = Collections.singletonList(task);

        when(taskService.getTasks(TaskStatus.IN_PROGRESS)).thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));
    }

    @Test
    void shouldGetTasksFilteredByDoneStatus() throws Exception {
        Task task = new Task(UUID.randomUUID(), "Task 1", "Desc 1", TaskStatus.DONE, Instant.now());
        List<Task> tasks = Collections.singletonList(task);

        when(taskService.getTasks(TaskStatus.DONE)).thenReturn(tasks);

        mockMvc.perform(get("/tasks")
                        .param("status", "DONE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("DONE"));
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() throws Exception {
        when(taskService.getTasks(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldGetTaskSummary() throws Exception {
        TaskSummaryResponse summary = new TaskSummaryResponse(10, 5, 3, 2);

        when(taskService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/tasks/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.todo").value(5))
                .andExpect(jsonPath("$.inProgress").value(3))
                .andExpect(jsonPath("$.done").value(2));
    }

    @Test
    void shouldGetEmptyTaskSummary() throws Exception {
        TaskSummaryResponse summary = new TaskSummaryResponse(0, 0, 0, 0);

        when(taskService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/tasks/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.todo").value(0))
                .andExpect(jsonPath("$.inProgress").value(0))
                .andExpect(jsonPath("$.done").value(0));
    }

    @Test
    void shouldUpdateTaskStatusSuccessfully() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.IN_PROGRESS);

        Task updatedTask = new Task(taskId, "Task", "Desc", TaskStatus.IN_PROGRESS, Instant.now());

        when(taskService.updateStatus(eq(taskId), eq(TaskStatus.IN_PROGRESS))).thenReturn(updatedTask);

        mockMvc.perform(patch("/tasks/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldUpdateTaskStatusToDone() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.DONE);

        Task updatedTask = new Task(taskId, "Task", "Desc", TaskStatus.DONE, Instant.now());

        when(taskService.updateStatus(eq(taskId), eq(TaskStatus.DONE))).thenReturn(updatedTask);

        mockMvc.perform(patch("/tasks/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.DONE);

        when(taskService.updateStatus(eq(taskId), eq(TaskStatus.DONE)))
                .thenThrow(new TaskNotFoundException(taskId));

        mockMvc.perform(patch("/tasks/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnBadRequestWhenStatusIsNull() throws Exception {
        UUID taskId = UUID.randomUUID();
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(null);

        mockMvc.perform(patch("/tasks/{id}/status", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleInvalidUuidFormat() throws Exception {
        UpdateTaskStatusRequest request = new UpdateTaskStatusRequest();
        request.setStatus(TaskStatus.DONE);

        mockMvc.perform(patch("/tasks/{id}/status", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}