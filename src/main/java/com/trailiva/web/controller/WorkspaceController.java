package com.trailiva.web.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.*;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.WorkspaceService;
import com.trailiva.util.Helper;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import java.io.IOException;
import java.util.List;

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
    public ResponseEntity<?> getPersonalWorkspacesByUserId(@CurrentUser UserPrincipal userPrincipal) {
        try {
             WorkSpace workSpace = workspaceService.getUserPersonalWorkspace(userPrincipal.getId());
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("my-workspace/{workspaceId}")
    @PreAuthorize("hasAnyRole('ROLE_SUPER_MODERATOR', 'ROLE_MODERATOR', 'ROLE_ADMIN')")

    public ResponseEntity<?> getWorkspace(@PathVariable Long workspaceId) {
        try {
            WorkSpace workSpace = workspaceService.getOfficialWorkspace(workspaceId);
            return new ResponseEntity<>(workSpace, HttpStatus.OK);
        } catch (WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("send-request-token/{workspaceId}")
    public ResponseEntity<?> sendRequestToken(@RequestParam("email") String email, @PathVariable Long workspaceId){
        try {
            workspaceService.sendWorkspaceRequestToken(workspaceId, email);
            return new ResponseEntity<>(new ApiResponse(true, "Request token send to member", HttpStatus.OK), HttpStatus.OK);
        } catch (WorkspaceException | UserException  e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("my-workspace/add-member")
    public ResponseEntity<?> addMember(@CurrentUser UserPrincipal userPrincipal, @RequestBody List<String> emails) {
        try {
            workspaceService.addMemberToOfficialWorkspace(emails, userPrincipal.getId());

            return new ResponseEntity<>(new ApiResponse(true, "members are successfully added to workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (WorkspaceException | UserException | BadRequestException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("my-workspace/add-moderator")
    public ResponseEntity<?> addModerator(@CurrentUser UserPrincipal userPrincipal, @RequestBody List<String> emails) {
        try {
           workspaceService.addModeratorToOfficialWorkspace(emails, userPrincipal.getId());
            return new ResponseEntity<>(new ApiResponse(true, "moderators are successfully added to workspace", HttpStatus.OK), HttpStatus.OK);
        } catch (WorkspaceException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("my-workspace/csv/add-member")
    public ResponseEntity<?> addMemberFromCSV(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file")MultipartFile file){
        try {
            if (Helper.hasCSVFormat(file)){
                workspaceService.addMemberToWorkspaceFromCSV(file, userPrincipal.getId());
                return new ResponseEntity<>(new ApiResponse(true, "members are successfully added to workspace", HttpStatus.OK), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Please upload a csv file!", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);

        } catch (CsvValidationException | IOException |UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("my-workspace/csv/add-moderator")
    public ResponseEntity<?> addModeratorFromCSV(@CurrentUser UserPrincipal userPrincipal, @RequestParam("file")MultipartFile file){
        try {
            if (Helper.hasCSVFormat(file)){
                workspaceService.addModeratorToWorkspaceFromCSV(file, userPrincipal.getId());
                return new ResponseEntity<>(new ApiResponse(true, "moderators are successfully added to workspace", HttpStatus.OK), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ApiResponse(true, "Please upload a csv file!", HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);

        } catch (CsvValidationException | IOException |UserException | WorkspaceException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
