package com.rag.ragbackend.service;

import com.rag.ragbackend.entity.Task;

import java.util.List;

public interface TaskService {

    Task createTask(Task task);

    Task updateTask(String taskId, Task task);

    List<Task> getTasksByProjectId(String projectId);
}