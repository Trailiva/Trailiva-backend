package com.trailiva.web.controller;


import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Task;
import com.trailiva.service.ProjectService;
import com.trailiva.service.TaskService;
import com.trailiva.util.AppConstants;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import com.trailiva.web.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/trailiva/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/create/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> register(@RequestBody @Valid TaskRequest request, @PathVariable Long workspaceId) {
        try {
            Task task = taskService.createTask(request, workspaceId);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (TaskException | ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long taskId) {
        try {
            Task task = taskService.updateTask(taskRequest, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{workspaceId}/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getTaskDetail(@PathVariable Long workspaceId, @PathVariable Long taskId) {
        try {
            Task task = taskService.getTaskDetail(workspaceId, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("workspace/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getAllTaskInWorkspace(@PathVariable Long workspaceId) {
        try {
            List<Task> tasks = taskService.getTasksByWorkspaceId(workspaceId);
            return ResponseEntity.ok(tasks);
        } catch (ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully deleted", HttpStatus.OK));
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping("/updateTab/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateTaskTag(@RequestParam String tab, @PathVariable Long taskId) {
        try {
            Task task = taskService.updateTaskTag(taskId, tab);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/priority/{taskPriority}/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateTaskPriority(@PathVariable Priority taskPriority, @PathVariable Long workspaceId) {
        try {
            List<Task> tasks = taskService.filterTaskByPriority(workspaceId, taskPriority);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (TaskException | ProjectException exception) {
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> searchForTask(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                           @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
                                           @RequestParam Map<String, String> params) {
        try {
            Map<String, Object> response = taskService.searchTaskByNameAndDescription(params, page, size);
            return new ResponseEntity<>(new ApiResponse(true, "Data successfully filtered", response), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
