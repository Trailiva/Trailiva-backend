package com.trailiva.service;


import com.trailiva.data.model.Task;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import com.trailiva.web.payload.response.TaskResponse;

public interface TaskService {
    Task createTask(TaskRequest request, Long id) throws TaskException, WorkspaceException;
}
