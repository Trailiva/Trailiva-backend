package com.trailiva.web.controller;

import com.trailiva.data.model.User;
import com.trailiva.data.model.WorkSpace;
import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.UserService;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.response.ApiResponse;
import com.trailiva.web.payload.response.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/users")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfile(@CurrentUser UserPrincipal currentUser) {
        try {
            User userProfile = userService.getUserProfile(currentUser.getId());
            Link link = linkTo(UserController.class)
                    .slash(userProfile.getUserId()).withSelfRel();
            userProfile.add(link);


            ResponseEntity<WorkSpace> methodLinkBuilder = (ResponseEntity<WorkSpace>) methodOn(WorkspaceController.class)
                    .getWorkspacesByUserId(userProfile.getUserId());

            Link workspaceLink = linkTo(methodLinkBuilder).withRel("user-workspace");

            userProfile.add(workspaceLink);

            return new ResponseEntity<>(userProfile, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId){
        try {
            UserProfile userProfile = userService.getUserDetails(userId);
            return new ResponseEntity<>(userProfile, HttpStatus.OK);

        }catch (UserException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}