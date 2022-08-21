package com.trailiva.web.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.*;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.WorkspaceService;
import com.trailiva.util.Helper;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import com.trailiva.web.payload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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


    @GetMapping("/my-workspace/personal")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getPersonalWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        try {
            WorkSpace workSpace = workspaceService.getUserPersonalWorkspace(userPrincipal.getId());
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-workspace/official")
    @PreAuthorize("hasRole('ROLE_SUPER_MODERATOR')")
    public ResponseEntity<?> getOfficialWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        try {
            WorkSpace workSpace = workspaceService.getUserOfficialWorkspace(userPrincipal.getId());
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/official/{workspaceId}")
    @PreAuthorize("hasRole('ROLE_SUPER_MODERATOR')")
    public ResponseEntity<?> getOfficialWorkspace(@PathVariable Long workspaceId) {
        try {
            WorkSpace workSpace = workspaceService.getOfficialWorkspace(workspaceId);
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/personal/{workspaceId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getPersonalWorkspace(@PathVariable Long workspaceId) {
        try {
            WorkSpace workSpace = workspaceService.getPersonalWorkspace(workspaceId);
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }



    @PostMapping("/my-workspace/add-contributor/request-token")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addContributor(@RequestParam("requestToken") String requestToken) {
        try {
            workspaceService.addContributorToWorkspace(requestToken);
            return new ResponseEntity<>(new ApiResponse(true, "contributor is successfully added to workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (TokenException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/my-workspace/add-moderator/request-token")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addModerator(@RequestParam("requestToken") String requestToken) {
        try {
            workspaceService.addModeratorToWorkspace(requestToken);
            return new ResponseEntity<>(new ApiResponse(true, "moderator is successfully added to workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (TokenException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/my-workspace/add-contributors")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addContributor(@CurrentUser UserPrincipal userPrincipal, @RequestBody List<String> emails) {
        try {
            workspaceService.addContributorToOfficialWorkspace(emails, userPrincipal.getId());

            return new ResponseEntity<>(new ApiResponse(true, "Request token send to contributor", HttpStatus.OK), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/add-moderators")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addModerator(@CurrentUser UserPrincipal userPrincipal, @RequestBody List<String> emails) {
        try {
            workspaceService.addModeratorToOfficialWorkspace(emails, userPrincipal.getId());
            return new ResponseEntity<>(new ApiResponse(true, "Request token send to moderator", HttpStatus.OK), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/csv/add-contributors")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addContributorFromCSV(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file") MultipartFile file) {
        try {
            if (Helper.hasCSVFormat(file)) {
                workspaceService.addContributorToWorkspaceFromCSV(file, userPrincipal.getId());
                return new ResponseEntity<>(new ApiResponse(true, "Request token send to contributor", HttpStatus.OK), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Please upload a csv file!", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);

        } catch (CsvValidationException | IOException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/csv/add-moderators")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addModeratorFromCSV(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file") MultipartFile file) {
        try {
            if (Helper.hasCSVFormat(file)) {
                workspaceService.addModeratorToWorkspaceFromCSV(file, userPrincipal.getId());
                return new ResponseEntity<>(new ApiResponse(true, "Request token send to moderator", HttpStatus.OK), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Please upload a csv file!", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);

        } catch (CsvValidationException | IOException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/my-workspace/remove-moderator/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> removeModerator(@PathVariable Long userId, @CurrentUser UserPrincipal userPrincipal) {
        try {
            workspaceService.removeModeratorFromWorkspace(userPrincipal.getId(), userId);
            return new ResponseEntity<>(new ApiResponse(true, "Moderator is successfully removed from workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/my-workspace/remove-contributor/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> removeContributor(@PathVariable Long userId, @CurrentUser UserPrincipal userPrincipal){
        try {
            workspaceService.removeContributorFromWorkspace(userPrincipal.getId(), userId);
            return new ResponseEntity<>(new ApiResponse(true, "Contributor is successfully removed from workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
