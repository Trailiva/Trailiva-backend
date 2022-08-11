package com.trailiva.service;

import com.trailiva.data.model.*;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.data.repository.WorkspaceRepository;
import com.trailiva.specification.TaskSpecifications;
import com.trailiva.util.Helper;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.trailiva.data.model.Tab.PENDING;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService{

    private final TaskRepository taskRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ModelMapper modelMapper;

    private static int taskReferenceId = 1;

    public TaskServiceImpl(TaskRepository taskRepository,
                           WorkspaceRepository workspaceRepository,
                           ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.workspaceRepository = workspaceRepository;
        this.modelMapper = modelMapper;
    }

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
    @Transactional
    public Task updateTask(TaskRequest taskRequest, Long id) throws TaskException {
        Task taskToUpdate = taskRepository.findById(id).orElseThrow(()-> new TaskException("Task does not exist"));
        modelMapper.map(taskRequest, taskToUpdate);
        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
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
    @Transactional
    public Task getTaskDetail(Long workspaceId, Long taskId)throws WorkspaceException {
        WorkSpace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("No workspace found"));
        Optional<Task> task = workspace.getTasks().stream().filter(data -> Objects.equals(data.getId(), taskId)).findFirst();
        return task.orElse(null);
    }

    @Override
    @Transactional
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
    @Transactional
    public List<Task> filterTaskByTab(Long workspaceId, Tab taskTab) throws WorkspaceException {
        WorkSpace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                ()-> new WorkspaceException("No workspace found"));
        return workspace.getTasks().stream()
                .filter(task -> task.getTab() == taskTab)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public List<Task> getDueTasks(LocalDate time) {
        return  taskRepository.findByDueDate(time);
    }

    @Override
    public Map<String, Object> searchTaskByNameAndDescription(Map<String, String> params, int page, int size) throws BadRequestException {
        Helper.validatePageNumberAndSize(page, size);
        Specification<Task> searchByName = TaskSpecifications.withTaskName(params.get("name"));
        Specification<Task> searchByDesc = TaskSpecifications.withTaskDescription(params.get("description"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        Page<Task> result = taskRepository.findAll(
                Specification.where(searchByName)
                        .and(searchByDesc),
                pageable
        );
        Map<String, Object> response = new HashMap<>();
        response.put("data", result.getContent());
        response.put("recordsTotal", result.getTotalElements());
        response.put("recordsFiltered", result.getTotalElements());
        return response;    }


}
