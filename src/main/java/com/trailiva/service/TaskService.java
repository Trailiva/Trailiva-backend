package com.trailiva.service;


import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Tab;
import com.trailiva.data.model.Task;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.payload.request.TaskRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TaskService {
    Task createTask(TaskRequest request, Long projectId) throws TaskException, ProjectException;
    Task updateTask(TaskRequest taskToUpdate, Long taskId) throws TaskException;
    void deleteTask(Long taskId) throws TaskException;
    Task getTaskDetail(Long workspaceId, Long taskId) throws ProjectException;
    Task updateTaskTag(Long taskId, String taskTag) throws TaskException;
    List<Task> filterTaskByPriority(Long workSpaceId, Priority taskPriority) throws TaskException, ProjectException;
    List<Task> filterTaskByTab(Long workspaceId,  Tab taskTab) throws TaskException, ProjectException;
    List<Task> getDueTasks(LocalDate time);
    Map<String, Object> searchTaskByNameAndDescription(Map<String, String> params, int page, int size) throws BadRequestException;
}
