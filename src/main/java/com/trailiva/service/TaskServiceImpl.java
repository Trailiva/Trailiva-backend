package com.trailiva.service;

import com.trailiva.data.model.*;
import com.trailiva.data.repository.TaskPriorityRepository;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.trailiva.data.model.Tab.PENDING;

@Service
@Slf4j
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
        WorkSpace workSpace = workspaceRepository.findById(workSpaceId).orElseThrow(()-> new WorkspaceException("Project not found"));

        Task newTask = modelMapper.map(request, Task.class);
        newTask.setTab(PENDING);

        Task task = taskRepository.save(newTask);
        workSpace.getTasks().add(newTask);
        workspaceRepository.save(workSpace);
        return task;
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
    public List<Task> getTasksByWorkspaceId(Long workspaceId) throws WorkspaceException {
        WorkSpace workspace = workspaceRepository.findById(workspaceId).orElseThrow(()-> new WorkspaceException("No workspace found"));
        return workspace.getTasks();
    }

    @Override
    public Task getTaskDetail(Long taskId) throws TaskException {
        return taskRepository.findById(taskId).orElseThrow(
                ()-> new TaskException("Task with id not found"));
    }

    @Override
    public Task updateTaskTag(Long taskId, String taskTab) throws TaskException {
        Task taskToUpdate = taskRepository.findById(taskId).orElseThrow(
                ()-> new TaskException("Task not found"));
        taskToUpdate.setTab(Tab.tabMapper(taskTab));
        return taskRepository.save(taskToUpdate);
    }

    @Override
    public List<Task> filterTaxByPriority(Priority taskPriority) throws TaskException {
        List<Task> allTask = taskRepository.findAll();
        List<Task> filteredTask = new ArrayList<>();
        allTask.forEach(task -> {
            if(task.getPriority().equals(taskPriority)) filteredTask.add(task);
        });
        return filteredTask;
    }


    private boolean existByName(String name) {
        return taskRepository.existsTaskByName(name);
    }





}
