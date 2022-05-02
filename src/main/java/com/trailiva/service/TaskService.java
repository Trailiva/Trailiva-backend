package com.trailiva.service;


import com.trailiva.data.model.Task;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import com.trailiva.web.payload.response.TaskResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TaskService {
    Task createTask(TaskRequest request, Long taskId) throws TaskException, WorkspaceException;
    Task updateTask(TaskRequest taskToUpdate, Long taskId) throws TaskException;
    void deleteTask(Long taskId) throws TaskException;
    List<Task> getTasksByWorkspaceId(Long workspaceId) throws WorkspaceException;
    Task getTaskDetail(Long taskId) throws TaskException;
    Task updateTaskTag(Long taskId, String taskTag) throws TaskException;
}
