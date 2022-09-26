package com.trailiva.web.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.Project;
import com.trailiva.data.model.Task;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.ProjectService;
import com.trailiva.util.Helper;
import com.trailiva.web.exceptions.ProjectException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.ProjectRequest;
import com.trailiva.web.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping("personal/create/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createProject(@Valid @PathVariable Long workspaceId, @RequestBody ProjectRequest request){
        try {
            Project project = projectService.createProjectForPersonalWorkspace(request, workspaceId);
            return  ResponseEntity.ok(project);
        } catch (WorkspaceException | UserException | ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
  @PostMapping("official/create/{workspaceId}")
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
  public ResponseEntity<?> createProjectForOfficialWorkspace(@Valid @PathVariable Long workspaceId, @RequestBody ProjectRequest request){
        try {
            Project project = projectService.createProjectForOfficialWorkspace(request, workspaceId);
            return  ResponseEntity.ok(project);
        } catch (WorkspaceException  | ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectsByProjectId(@PathVariable Long projectId) {
        try {
            Project project = projectService.getProjectById(projectId);
            return  ResponseEntity.ok(project);
        } catch ( ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("project/{projectId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getAllTaskInProject(@PathVariable Long projectId) {
        try {
            List<Task> tasks = projectService.getTasksByProjectId(projectId);
            return ResponseEntity.ok(tasks);
        } catch (ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("count-tasks/{projectId}")
    public ResponseEntity<?> getTaskCount(@PathVariable Long projectId) {
        try {
            int taskCount = projectService.countProjectTask(projectId);
            return  ResponseEntity.ok(new ApiResponse(true, "Task is successfully counted", taskCount));
        } catch ( ProjectException e) {
            return  new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add-contributor/request-token")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> addModerator(@RequestParam("requestToken") String requestToken) {
        try {
            projectService.addContributor(requestToken);
            return new ResponseEntity<>(new ApiResponse(true, "contributor is successfully added to project"), HttpStatus.OK);
        } catch (TokenException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add-contributors/{projectId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> addContributor(@RequestBody List<String> emails, @PathVariable Long projectId) {
        try {
            projectService.addContributor(emails, projectId);

            return new ResponseEntity<>(new ApiResponse(true, "Request token send to contributor"), HttpStatus.OK);
        } catch (UserException | ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/csv/add-contributors/{projectId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<?> addContributorFromCSV(@RequestParam("file") MultipartFile file,  @PathVariable Long projectId) {
        try {
            if (Helper.hasCSVFormat(file)) {
                projectService.addContributorFromCSV(file, projectId);
                return new ResponseEntity<>(new ApiResponse(true, "Request token send to contributor"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Please upload a csv file!"), HttpStatus.BAD_REQUEST);

        } catch (CsvValidationException | IOException | UserException | ProjectException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


}
