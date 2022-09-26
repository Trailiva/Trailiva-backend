package com.trailiva.web.controller.workspace;

import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.workspace.PersonalWorkspaceService;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import com.trailiva.web.payload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("api/v1/trailiva/personal")
public class PersonalWorkspaceController {
    private final PersonalWorkspaceService personalWorkspaceService;

    public PersonalWorkspaceController(PersonalWorkspaceService personalWorkspaceService) {
        this.personalWorkspaceService = personalWorkspaceService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createPersonalWorkspace(@CurrentUser UserPrincipal currentUser, @RequestBody @Valid WorkspaceRequest request) {
        try {
            String referenceName = request.getName().substring(0, 2).toUpperCase();
            request.setReferenceName(referenceName);
            PersonalWorkspace workSpace = personalWorkspaceService.createPersonalWorkspace(request, currentUser.getId());
            return ResponseEntity.ok(workSpace);
        } catch (WorkspaceException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-workspace")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getPersonalWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        try {
            PersonalWorkspace workSpace = personalWorkspaceService.getUserWorkspace(userPrincipal.getId());
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("count-projects/{workspaceId}")
    public ResponseEntity<?> getProjectCount(@PathVariable Long workspaceId) {
        try {
            int projectCount = personalWorkspaceService.countWorkspaceProjects(workspaceId);
            return ResponseEntity.ok(new ApiResponse(true, "Successful", projectCount));
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllWorkspace() {
        List<PersonalWorkspace> personalWorkspaces = personalWorkspaceService.getAllWorkspace();
        Map<String, List<?>> userCount = Map.of("personalWorkspaces", personalWorkspaces);
        return ResponseEntity.ok(new ApiResponse(true, "Successful", userCount));
    }


}
