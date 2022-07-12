package com.trailiva.web.controller;


import com.trailiva.data.model.Priority;
import com.trailiva.data.model.Task;
import com.trailiva.service.TaskService;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.TaskRequest;
import com.trailiva.web.payload.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/trailiva/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/create/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> register(@RequestBody @Valid TaskRequest request, @PathVariable Long workspaceId) {
        try {
            Task task = taskService.createTask(request, workspaceId);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (WorkspaceException | TaskException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long taskId) {
        try {
            Task task = taskService.updateTask(taskRequest, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("{workspaceId}/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getTaskDetail(@PathVariable Long workspaceId, @PathVariable Long taskId) {
        try {
            Task task = taskService.getTaskDetail(workspaceId, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("workspace/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getAllTaskInWorkspace(@PathVariable Long workspaceId) {
        try {
            List<Task> tasks = taskService.getTasksByWorkspaceId(workspaceId);
            return ResponseEntity.ok(tasks);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Task is successfully deleted", HttpStatus.OK));
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PatchMapping("/updateTab/{taskId}")
    public ResponseEntity<?> updateTaskTag(@RequestParam String tab, @PathVariable Long taskId) {
        try {
            Task task = taskService.updateTaskTag(taskId, tab);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/updateTab/{workspaceId}")
    public ResponseEntity<?> updateTaskPriority(@RequestParam Priority taskPriority, @PathVariable Long workspaceId) {
        try {
            List<Task> tasks = taskService.filterTaskByPriority(workspaceId, taskPriority);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (TaskException | WorkspaceException exception) {
            return new ResponseEntity<>(new ApiResponse<>(false, exception.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
