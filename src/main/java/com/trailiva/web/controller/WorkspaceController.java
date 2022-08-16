package com.trailiva.web.controller;

import com.trailiva.data.model.Project;
import com.trailiva.data.model.Task;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.WorkspaceService;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import com.trailiva.web.payload.response.ApiResponse;
import com.trailiva.web.payload.response.WorkspaceList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("api/v1/trailiva/workspace")
public class WorkspaceController {


    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createWorkspace(@CurrentUser UserPrincipal currentUser, @RequestBody @Valid WorkspaceRequest request) {
        try {
            String referenceName = request.getName().substring(0, 2).toUpperCase();
            request.setReferenceName(referenceName);
            WorkSpace workSpace = workspaceService.createWorkspace(request, currentUser.getId());
            return ResponseEntity.ok(workSpace);
        } catch (WorkspaceException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        WorkspaceList workSpaceList = new WorkspaceList();
        try {
            Set<WorkSpace> workSpaces = workspaceService.getWorkspaces(userPrincipal.getId());

            for (WorkSpace workSpace : workSpaces) {

                ResponseEntity<WorkSpace> getWorkspaceLink = (ResponseEntity<WorkSpace>) methodOn(WorkspaceController.class).getWorkspace(workSpace.getWorkspaceId());
                Link getSpaceLink = linkTo(getWorkspaceLink).withRel("my-workspace");
                workSpace.add(getSpaceLink);

                ResponseEntity<Project> getProjectsLink =
                        methodOn(ProjectController.class)
                                .getProjectsByWorkspaceId(workSpace.getWorkspaceId());

                Link projectLink = linkTo(getProjectsLink).withRel("workspace-projects");

                workSpace.add(projectLink);
                workSpaceList.getWorkSpaces().add(workSpace);
            }

            Link selfLink =
                    linkTo(methodOn(WorkspaceController.class).getWorkspacesByUserId(userPrincipal)).withSelfRel();

            workSpaceList.add(selfLink);

            return new ResponseEntity<>(workSpaceList, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("my-workspace/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_MODERATOR', 'ROLE_ADMIN')")

    public ResponseEntity<?> getWorkspace(@PathVariable Long workspaceId) {
        try {
            WorkSpace workSpace = workspaceService.getOfficialWorkspace(workspaceId);

            ResponseEntity<Task> getTasksLink = (ResponseEntity<Task>) methodOn(TaskController.class).getAllTaskInWorkspace(workSpace.getWorkspaceId());

            Link projectLink = linkTo(getTasksLink).withRel("workspace-tasks");

            workSpace.add(projectLink);

            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("my-workspace/add-member/{workspaceId}")
    public ResponseEntity<?> addMember(@PathVariable Long workspaceId, @RequestParam String email) {
        try {
            WorkSpace workSpace = workspaceService.addMemberToOfficialWorkspace(workspaceId, email);

            ResponseEntity<Task> getTasksLink = (ResponseEntity<Task>) methodOn(TaskController.class).getAllTaskInWorkspace(workSpace.getWorkspaceId());

            Link projectLink = linkTo(getTasksLink).withRel("workspace-tasks");

            workSpace.add(projectLink);

            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException | UserException | BadRequestException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
