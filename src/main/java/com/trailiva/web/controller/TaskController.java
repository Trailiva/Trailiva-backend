package com.trailiva.web.controller;


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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/create/{workspaceId}")
     @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> register(@Valid @RequestBody TaskRequest request, @PathVariable Long workspaceId) {
        try {
            Task task = taskService.createTask(request, workspaceId);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (WorkspaceException | TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@Valid @RequestBody TaskRequest taskRequest, @PathVariable Long taskId){
        try {
            Task task = taskService.updateTask(taskRequest, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetail(@Valid @PathVariable Long taskId){
        try {
            Task task = taskService.getTaskDetail(taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("workspace/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getAllTaskInWorkspace(@Valid @PathVariable Long workspaceId){
        try {
            List<Task> tasks = taskService.getTasksByWorkspaceId(workspaceId);
            return ResponseEntity.ok(tasks);
        }
        catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(),  HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@Valid @PathVariable Long taskId){
        try {
            taskService.deleteTask(taskId);
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully deleted",  HttpStatus.OK));
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(),  HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
