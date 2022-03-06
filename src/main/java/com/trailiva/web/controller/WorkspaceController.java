package com.trailiva.web.controller;

import com.trailiva.service.WorkspaceService;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import com.trailiva.web.payload.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @PostMapping("/create/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createWorkspace(@Valid @PathVariable Long id, @RequestBody WorkspaceRequest request){
        try {
            workspaceService.create(request, id);
            return  new ResponseEntity<>(new ApiResponse(true, "Successfully created workspace"), HttpStatus.CREATED);
        } catch (WorkspaceException | UserException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
