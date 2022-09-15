package com.trailiva.web.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.WorkspaceService;
import com.trailiva.util.Helper;
import com.trailiva.web.exceptions.TaskException;
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
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("api/v1/trailiva/workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }


    @PostMapping("/personal/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createPersonalWorkspace(@CurrentUser UserPrincipal currentUser, @RequestBody @Valid WorkspaceRequest request) {
        try {
            String referenceName = request.getName().substring(0, 2).toUpperCase();
            request.setReferenceName(referenceName);
            PersonalWorkspace workSpace = workspaceService.createPersonalWorkspace(request, currentUser.getId());
            return ResponseEntity.ok(workSpace);
        } catch (WorkspaceException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/official/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createOfficialWorkspace(@CurrentUser UserPrincipal currentUser, @RequestBody @Valid WorkspaceRequest request) {
        try {
            String referenceName = request.getName().substring(0, 2).toUpperCase();
            request.setReferenceName(referenceName);
            OfficialWorkspace workSpace = workspaceService.createOfficialWorkspace(request, currentUser.getId());
            return ResponseEntity.ok(workSpace);
        } catch (WorkspaceException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/my-workspace/personal")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getPersonalWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        try {
            PersonalWorkspace workSpace = workspaceService.getUserPersonalWorkspace(userPrincipal.getId());
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-workspace/official")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getOfficialWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        try {
            OfficialWorkspace workSpace = workspaceService.getUserOfficialWorkspace(userPrincipal.getId());
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/official/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getOfficialWorkspace(@PathVariable Long workspaceId) {
        try {
            OfficialWorkspace workSpace = workspaceService.getOfficialWorkspace(workspaceId);
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/personal/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getPersonalWorkspace(@PathVariable Long workspaceId) {
        try {
            PersonalWorkspace workSpace = workspaceService.getPersonalWorkspace(workspaceId);
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/my-workspace/add-contributor/request-token")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> addContributor(@RequestParam("requestToken") String requestToken) {
        try {
            workspaceService.addContributorToWorkspace(requestToken);
            return new ResponseEntity<>(new ApiResponse(true, "contributor is successfully added to workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (TokenException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/my-workspace/add-moderator/request-token")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
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
    public ResponseEntity<?> removeContributor(@PathVariable Long userId, @CurrentUser UserPrincipal userPrincipal) {
        try {
            workspaceService.removeContributorFromWorkspace(userPrincipal.getId(), userId);
            return new ResponseEntity<>(new ApiResponse(true, "Contributor is successfully removed from workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("count-projects/official/{workspaceId}")
    public ResponseEntity<?> getProjectCountForOfficialWorkspace(@PathVariable Long workspaceId) {
        try {
            int projectCount = workspaceService.countOfficialWorkspaceProject(workspaceId);
            return ResponseEntity.ok(new ApiResponse(true, "Successful", projectCount));
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("count-projects/personal/{workspaceId}")
    public ResponseEntity<?> getProjectCountForPersonalWorkspace(@PathVariable Long workspaceId) {
        try {
            int projectCount = workspaceService.countPersonalWorkspaceProject(workspaceId);
            return ResponseEntity.ok(new ApiResponse(true, "Successful", projectCount));
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("count-users/official/{workspaceId}")
    public ResponseEntity<?> countAllContributorAndModeratorOnOfficialWorkspace(@PathVariable Long workspaceId) {
        try {
            int contributorCount = workspaceService.countContributorOnOfficialWorkspace(workspaceId);
            int moderatorCount = workspaceService.countModeratorOnOfficialWorkspace(workspaceId);
            Map<String, Integer> userCount = Map.of("contributorCount", contributorCount, "moderatorCount", moderatorCount);
            return ResponseEntity.ok(new ApiResponse(true, "Successful", userCount));
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllWorkspace() {
        List<PersonalWorkspace> personalWorkspaces = workspaceService.getPersonalWorkspaces();
        List<OfficialWorkspace> officialWorkspaces = workspaceService.getOfficialWorkspaces();
        Map<String, List<?>> userCount = Map.of("personalWorkspaces", personalWorkspaces, "officialWorkspaces", officialWorkspaces);
        return ResponseEntity.ok(new ApiResponse(true, "Successful", userCount));
    }

    @PostMapping("/official/assign-task/{workspaceId}/{taskId}/{contributorId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> assignTaskToContributorOnOfficialWorkspace(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long workspaceId,
                                                                        @PathVariable Long contributorId, @PathVariable Long taskId) {
        try {
            workspaceService.assignContributorToTaskOnOfficialWorkspace(userPrincipal.getId(), contributorId, taskId, workspaceId);
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully assigned to contributor"));
        } catch (TaskException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/official/request-task/{workspaceId}/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> requestTaskOnOfficialWorkspace(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long workspaceId, @PathVariable Long taskId) {
        try {
            workspaceService.requestTask(workspaceId, taskId, userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully requested"));
        } catch (TaskException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/official/assign-task")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> assignTaskToContributorOnOfficialWorkspace(@CurrentUser UserPrincipal userPrincipal, @RequestParam("token") String token) {
        try {
            workspaceService.assignTaskToContributorWithRequestToken(userPrincipal.getId(), token);
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully assigned to contributor"));
        } catch (TaskException | UserException | TokenException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
