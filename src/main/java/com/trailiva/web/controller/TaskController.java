package com.trailiva.web.controller;


import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Task;
import com.trailiva.service.TaskService;
import com.trailiva.util.AppConstants;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.TaskException;
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

    @PostMapping("/create/{projectId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> register(@RequestBody @Valid TaskRequest request, @PathVariable Long projectId) {
        try {
            Task task = taskService.createTask(request, projectId);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (TaskException | ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long taskId) {
        try {
            Task task = taskService.updateTask(taskRequest, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{projectId}/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getTaskDetail(@PathVariable Long projectId, @PathVariable Long taskId) {
        try {
            Task task = taskService.getTaskDetail(projectId, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully deleted"));
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping("/updateTab/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateTaskTag(@RequestParam String tab, @PathVariable Long taskId) {
        try {
            Task task = taskService.updateTaskTag(taskId, tab);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/priority/{taskPriority}/{projectId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateTaskPriority(@PathVariable Priority taskPriority, @PathVariable Long projectId) {
        try {
            List<Task> tasks = taskService.filterTaskByPriority(projectId, taskPriority);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (TaskException | ProjectException exception) {
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage()), HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
