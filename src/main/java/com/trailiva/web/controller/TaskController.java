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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @PostMapping("/create/{workspaceId}")
    public ResponseEntity<?> register(@Valid @RequestBody TaskRequest request, @PathVariable Long workspaceId) {
        try {
            Task task = taskService.createTask(request, workspaceId);
            return new ResponseEntity<>(task, HttpStatus.CREATED);
        } catch (WorkspaceException | TaskException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
