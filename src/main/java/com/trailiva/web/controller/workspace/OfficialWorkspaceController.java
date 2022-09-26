package com.trailiva.web.controller.workspace;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.workspace.OfficialWorkspaceService;
import com.trailiva.util.Helper;
import com.trailiva.web.exceptions.TaskException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.AssignTaskRequest;
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
@RequestMapping("api/v1/trailiva/official")
public class OfficialWorkspaceController {
    private final OfficialWorkspaceService officialWorkspaceService;

    public OfficialWorkspaceController(OfficialWorkspaceService officialWorkspaceService) {
        this.officialWorkspaceService = officialWorkspaceService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> createOfficialWorkspace(@CurrentUser UserPrincipal currentUser, @RequestBody @Valid WorkspaceRequest request) {
        try {
            String referenceName = request.getName().substring(0, 2).toUpperCase();
            request.setReferenceName(referenceName);
            OfficialWorkspace workSpace = officialWorkspaceService.createOfficialWorkspace(request, currentUser.getId());
            return ResponseEntity.ok(workSpace);
        } catch (WorkspaceException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/my-workspace")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getOfficialWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        try {
            OfficialWorkspace workSpace = officialWorkspaceService.getUserOfficialWorkspace(userPrincipal.getId());
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getOfficialWorkspace(@PathVariable Long workspaceId) {
        try {
            OfficialWorkspace workSpace = officialWorkspaceService.getOfficialWorkspace(workspaceId);
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/add-contributors")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addContributor(@CurrentUser UserPrincipal userPrincipal, @RequestBody List<String> emails) {
        try {
           officialWorkspaceService.addContributor(emails, userPrincipal.getId());

            return new ResponseEntity<>(new ApiResponse(true, "Request token send to contributor"), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/add-moderators")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addModerator(@CurrentUser UserPrincipal userPrincipal, @RequestBody List<String> emails) {
        try {
            officialWorkspaceService.addModerator(emails, userPrincipal.getId());
            return new ResponseEntity<>(new ApiResponse(true, "Request token send to moderator"), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/csv/add-moderators")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addModeratorFromCSV(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file") MultipartFile file) {
        try {
            if (Helper.hasCSVFormat(file)) {
                officialWorkspaceService.addModeratorFromCSV(file, userPrincipal.getId());
                return new ResponseEntity<>(new ApiResponse(true, "Request token send to moderator"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Please upload a csv file!"), HttpStatus.BAD_REQUEST);

        } catch (CsvValidationException | IOException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/my-workspace/remove-moderator/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> removeModerator(@PathVariable Long userId, @CurrentUser UserPrincipal userPrincipal) {
        try {
            officialWorkspaceService.removeModerator(userPrincipal.getId(), userId);
            return new ResponseEntity<>(new ApiResponse(true, "Moderator is successfully removed from workspace"), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/my-workspace/remove-contributor/{contributorId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> removeContributor(@PathVariable Long contributorId, @CurrentUser UserPrincipal userPrincipal) {
        try {
            officialWorkspaceService.removeContributor(userPrincipal.getId(), contributorId);
            return new ResponseEntity<>(new ApiResponse(true, "Contributor is successfully removed from workspace"), HttpStatus.OK);
        } catch (UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/csv/add-contributors")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> addContributorFromCSV(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file") MultipartFile file) {
        try {
            if (Helper.hasCSVFormat(file)) {
                officialWorkspaceService.addContributorFromCSV(file, userPrincipal.getId());
                return new ResponseEntity<>(new ApiResponse(true, "Request token send to contributor"), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Please upload a csv file!"), HttpStatus.BAD_REQUEST);

        } catch (CsvValidationException | IOException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/add-contributor/request-token")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> addContributor(@RequestParam("requestToken") String requestToken) {
        try {
            officialWorkspaceService.addContributor(requestToken);
            return new ResponseEntity<>(new ApiResponse(true, "contributor is successfully added to workspace"), HttpStatus.OK);
        } catch (TokenException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/my-workspace/add-moderator/request-token")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> addModerator(@RequestParam("requestToken") String requestToken) {
        try {
            officialWorkspaceService.addModerator(requestToken);
            return new ResponseEntity<>(new ApiResponse(true, "moderator is successfully added to workspace"), HttpStatus.OK);
        } catch (TokenException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("count-projects/{workspaceId}")
    public ResponseEntity<?> getProjectCountForOfficialWorkspace(@PathVariable Long workspaceId) {
        try {
            int projectCount = officialWorkspaceService.countProject(workspaceId);
            return ResponseEntity.ok(new ApiResponse(true, "Successful", projectCount));
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("count-users/{workspaceId}")
    public ResponseEntity<?> countAllContributorAndModerator(@PathVariable Long workspaceId) {
        try {
            int contributorCount = officialWorkspaceService.countContributor(workspaceId);
            int moderatorCount = officialWorkspaceService.countModerator(workspaceId);
            Map<String, Integer> userCount = Map.of("contributorCount", contributorCount, "moderatorCount", moderatorCount);
            return ResponseEntity.ok(new ApiResponse(true, "Successful", userCount));
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllWorkspace() {
        List<OfficialWorkspace> officialWorkspaces = officialWorkspaceService.getWorkspace();
        Map<String, List<?>> workspaces = Map.of("officialWorkspaces", officialWorkspaces);
        return ResponseEntity.ok(new ApiResponse(true, "Successful", workspaces));
    }


    @PostMapping("/request-task/{workspaceId}/{taskId}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> requestTask(@CurrentUser UserPrincipal userPrincipal, @PathVariable Long workspaceId, @PathVariable Long taskId) {
        try {
            officialWorkspaceService.requestTask(workspaceId, taskId, userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully requested"));
        } catch (TaskException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/assign-task")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> assignTaskToContributorWithRequestToken(@CurrentUser UserPrincipal userPrincipal, @RequestParam("token") String token) {
        try {
            officialWorkspaceService.assignTaskToContributorWithRequestToken(userPrincipal.getId(), token);
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully assigned to contributor"));
        } catch (TaskException | UserException | TokenException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/assign-task/{}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> assignTaskToContributor(@CurrentUser UserPrincipal userPrincipal, @RequestBody AssignTaskRequest request) {
        try {
            officialWorkspaceService.assignContributorToTask(request, userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponse(true, "Task is successfully assigned to contributor"));
        } catch (TaskException | UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
