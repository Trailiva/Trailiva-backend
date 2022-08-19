package com.trailiva.service;

import com.trailiva.data.model.*;
import com.trailiva.data.repository.ProjectRepository;
import com.trailiva.data.repository.TaskRepository;
import com.trailiva.data.repository.PersonalWorkspaceRepository;
import com.trailiva.specification.TaskSpecifications;
import com.trailiva.util.Helper;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
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
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    private static int taskReferenceId = 1;

    public TaskServiceImpl(TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Task createTask(TaskRequest request, Long projectId) throws TaskException, ProjectException {
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new ProjectException("project not found"));
        boolean existByName = false;


        if (project.getTasks().size() > 0)
            existByName = project.getTasks().stream().anyMatch(task -> task.getName().equals(request.getName()));

        if (existByName)  throw new TaskException("This task already exist");

        Task newTask = modelMapper.map(request, Task.class);
        newTask.setPriority(Priority.fetchPriority(request.getPriority()).toString());
        newTask.setTab(PENDING.toString());
        String formattedId = String.format("%02d", taskReferenceId);
        taskReferenceId++;
        newTask.setTaskReference(project.getReferenceName().concat("-").concat(formattedId));

        Task task = taskRepository.save(newTask);
        project.getTasks().add(newTask);
        projectRepository.save(project);
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
    public List<Task> getTasksByWorkspaceId(Long projectId) throws ProjectException {
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()-> new ProjectException("Project not found"));
        return project.getTasks();
    }

    @Override
    @Transactional
    public Task getTaskDetail(Long projectId, Long taskId) throws ProjectException {
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()-> new ProjectException("Project not found"));
        Optional<Task> task = project.getTasks().stream().filter(data -> Objects.equals(data.getId(), taskId)).findFirst();
        return task.orElse(null);
    }

    @Override
    @Transactional
    public Task updateTaskTag(Long taskId, String taskTab) throws TaskException {
        Task taskToUpdate = taskRepository.findById(taskId).orElseThrow(
                ()-> new TaskException("Task not found"));
        taskToUpdate.setTab(Tab.tabMapper(taskTab).toString());
        return taskRepository.save(taskToUpdate);
    }

    @Override
    @Transactional
    public List<Task> filterTaskByPriority(Long projectId, Priority taskPriority) throws ProjectException {
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()-> new ProjectException("Project not found"));
        return project.getTasks().stream()
                .filter(task -> task.getPriority().equals( taskPriority.toString()))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Transactional
    public List<Task> filterTaskByTab(Long projectId, Tab taskTab) throws  ProjectException {
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()-> new ProjectException("Project not found"));
        return project.getTasks().stream()
                .filter(task -> task.getTab().equals(taskTab.toString()))
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
