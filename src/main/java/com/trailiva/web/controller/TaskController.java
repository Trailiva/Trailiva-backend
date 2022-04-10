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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/create/{projectId}")
    public ResponseEntity<?> register(@Valid @RequestBody TaskRequest request, @PathVariable Long projectId) {
        try {
            Task task = taskService.createTask(request, projectId);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (WorkspaceException | TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@Valid @RequestBody TaskRequest taskRequest, @PathVariable Long taskId){
        try {
            Task task = taskService.updateTask(taskRequest, taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskDetail(@Valid @PathVariable Long taskId){
        try {
            Task task = taskService.getTaskDetail(taskId);
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getAllTaskInProject(@Valid @PathVariable Long projectId){
        List<Task> task = taskService.getAllProjectTask(projectId);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@Valid @PathVariable Long taskId){
        try {
            taskService.deleteTask(taskId);
            return new ResponseEntity<>(new ApiResponse(true, "Task is successfully deleted"), HttpStatus.OK);
        } catch (TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
