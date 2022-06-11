package com.trailiva.service;


import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Tab;
import com.trailiva.data.model.Task;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;

import java.util.List;

public interface TaskService {
    Task createTask(TaskRequest request, Long taskId) throws TaskException, WorkspaceException;
    Task updateTask(TaskRequest taskToUpdate, Long taskId) throws TaskException;
    void deleteTask(Long taskId) throws TaskException;
    List<Task> getTasksByWorkspaceId(Long workspaceId) throws WorkspaceException;
    Task getTaskDetail(Long taskId) throws TaskException;
    Task updateTaskTag(Long taskId, String taskTag) throws TaskException;
    List<Task> filterTaskByPriority(Long workSpaceId, Priority taskPriority) throws TaskException, WorkspaceException;
    List<Task> filterTaskByTab(Long workspaceId,  Tab taskTab) throws TaskException, WorkspaceException;
}
