package com.trailiva.service;

import com.trailiva.data.model.*;
import com.trailiva.data.repository.ProjectRepository;
import com.trailiva.data.repository.TaskPriorityRepository;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import com.trailiva.web.payload.response.TaskResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;


    @Override
    public Task createTask(TaskRequest request, Long workSpaceId) throws TaskException, WorkspaceException {
        if (existByName(request.getName())) throw new TaskException("This task already exist");
        Project project = projectRepository.findById(workSpaceId).orElseThrow(()-> new WorkspaceException("Project not found"));

        Priority priority = Priority.fetchPriority(request.getPriority());
        PriorityField priorityField = new PriorityField(priority);
        taskPriorityRepository.save(priorityField);

        Task newTask = modelMapper.map(request, Task.class);
        newTask.setPriorityField(priorityField);

        taskRepository.save(newTask);
        project.getTasks().add(newTask);
        projectRepository.save(project);
        return newTask;
    }

    @Override
    public Task updateTask(TaskRequest taskRequest, Long id) throws TaskException {
        Task taskToUpdate = taskRepository.findById(id).orElseThrow(
                ()-> new TaskException("Task not found"));
        modelMapper.map(taskRequest, taskToUpdate);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    public void deleteTask(Long id) throws TaskException {
        Task taskToDelete = taskRepository.findById(id).orElseThrow(
                ()-> new TaskException("Task not found"));
        taskRepository.delete(taskToDelete);
    }

    @Override
    public List<Task> getAllProjectTask(Long projectId) {
        Project project = projectRepository.getById(projectId);
        return project.getTasks();
    }

    @Override
    public Task getTaskDetail(Long taskId) throws TaskException {
        return taskRepository.findById(taskId).orElseThrow(
                ()-> new TaskException("Task not found"));
    }


    private boolean existByName(String name) {
        return taskRepository.existsTaskByName(name);
    }





}
