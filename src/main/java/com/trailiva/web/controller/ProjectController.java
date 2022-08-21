package com.trailiva.web.controller;

import com.trailiva.data.model.Project;
import com.trailiva.service.ProjectService;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;
import com.trailiva.web.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping("personal/create/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createProject(@Valid @PathVariable Long id, @RequestBody ProjectRequest request){
        try {
            Project project = projectService.createProjectForPersonalWorkspace(request, id);
            return  ResponseEntity.ok(project);
        } catch (WorkspaceException | UserException | ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
  @PostMapping("official/create/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createProjectForOfficialWorkspace(@Valid @PathVariable Long id, @RequestBody ProjectRequest request){
        try {
            Project project = projectService.createProjectForOfficialWorkspace(request, id);
            return  ResponseEntity.ok(project);
        } catch (WorkspaceException  | ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<Project> getProjectsByWorkspaceId(@PathVariable Long workspaceId) {
        return new ResponseEntity<>(new Project(), HttpStatus.OK);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectsByProjectId(@PathVariable Long projectId) {
        try {
            Project project = projectService.getProjectById(projectId);
            return  ResponseEntity.ok(project);
        } catch ( ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("count-tasks/{projectId}")
    public ResponseEntity<?> getTaskCount(@PathVariable Long projectId) {
        try {
            int taskCount = projectService.countProjectTask(projectId);
            return  ResponseEntity.ok(new ApiResponse(true, "Task is successfully counted", taskCount));
        } catch ( ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


}
