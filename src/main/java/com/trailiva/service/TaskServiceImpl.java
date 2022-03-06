package com.trailiva.service;

import com.trailiva.data.model.Priority;
import com.trailiva.data.model.PriorityField;
import com.trailiva.data.model.Task;
import com.trailiva.data.model.WorkSpace;
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

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskPriorityRepository taskPriorityRepository;


    @Override
    public Task createTask(TaskRequest request, Long workSpaceId) throws TaskException, WorkspaceException {
        if (existByName(request.getName())) throw new TaskException("This task already exist");
        WorkSpace workSpace = workspaceRepository.findById(workSpaceId).orElseThrow(()-> new WorkspaceException("Workspace not found"));

        Priority priority = Priority.fetchPriority(request.getPriority());
        PriorityField priorityField = new PriorityField(priority);
        taskPriorityRepository.save(priorityField);

        Task newTask = modelMapper.map(request, Task.class);
        newTask.setPriorityField(priorityField);

        taskRepository.save(newTask);
        workSpace.getTasks().add(newTask);
        workspaceRepository.save(workSpace);
        return newTask;
    }

    private boolean existByName(String name) {
        return taskRepository.existsTaskByName(name);
    }
}
