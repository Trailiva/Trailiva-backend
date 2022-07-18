package com.trailiva.service;

import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Tab;
import com.trailiva.data.model.Task;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private static int taskReferenceId = 1;

    @Override
    @Transactional
    public Task createTask(TaskRequest request, Long workSpaceId) throws TaskException, WorkspaceException {
        WorkSpace workSpace = workspaceRepository.findById(workSpaceId).orElseThrow(()-> new WorkspaceException("workspace not found"));
        boolean existByName = false;


        if (workSpace.getTasks().size() > 0)
            existByName = workSpace.getTasks().stream().anyMatch(task -> task.getName().equals(request.getName()));

        if (existByName)  throw new TaskException("This task already exist");

        Task newTask = modelMapper.map(request, Task.class);
        newTask.setPriority(Priority.fetchPriority(request.getPriority()));
        newTask.setTab(PENDING);
        String formattedId = String.format("%02d", taskReferenceId);
        taskReferenceId++;
        newTask.setTaskReference(workSpace.getReferenceName().concat("-").concat(formattedId));

        Task task = taskRepository.save(newTask);
        workSpace.getTasks().add(newTask);
        workspaceRepository.save(workSpace);
        return task;
    }

    @Override
    public Task updateTask(TaskRequest taskRequest, Long id) throws TaskException {
        Task taskToUpdate = taskRepository.findById(id).orElseThrow(()-> new TaskException("Task does not exist"));
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
    @Transactional
    public List<Task> getTasksByWorkspaceId(Long workspaceId) throws WorkspaceException {
        WorkSpace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("No workspace found"));
        return workspace.getTasks();
    }

    @Override
    public Task getTaskDetail(Long workspaceId, Long taskId)throws WorkspaceException {
        WorkSpace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("No workspace found"));
        Optional<Task> task = workspace.getTasks().stream().filter(data -> Objects.equals(data.getId(), taskId)).findFirst();
        return task.orElse(null);
    }

    @Override
    public Task updateTaskTag(Long taskId, String taskTab) throws TaskException {
        Task taskToUpdate = taskRepository.findById(taskId).orElseThrow(
                ()-> new TaskException("Task not found"));
        taskToUpdate.setTab(Tab.tabMapper(taskTab));
        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public List<Task> filterTaskByPriority(Long workspaceId, Priority taskPriority) throws WorkspaceException {
        WorkSpace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("No workspace found"));
        return workspace.getTasks().stream()
                .filter(task -> task.getPriority() == taskPriority)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Task> filterTaskByTab(Long workspaceId, Tab taskTab) throws WorkspaceException {
        WorkSpace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("No workspace found"));
        return workspace.getTasks().stream()
                .filter(task -> task.getTab() == taskTab)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Task> getDueTasks(LocalDate time) {
        return  taskRepository.findByDueDate(time);
    }

    @Override
    public Task searchTaskByName(String taskName) throws TaskException {
        return taskRepository.findByName(taskName).orElseThrow(()-> new TaskException("No task with the task name found"));
    }

    @Override
    public Task searchTaskByDescription(String description) {
        return null;
    }


}
