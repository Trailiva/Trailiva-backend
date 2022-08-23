package com.trailiva.web.controller;

import com.trailiva.security.CurrentUser;
import com.trailiva.security.UserPrincipal;
import com.trailiva.service.CloudinaryService;
import com.trailiva.service.UserService;
import com.trailiva.util.AppConstants;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.request.ImageRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    public UserController(UserService userService, CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getUserProfile(@CurrentUser UserPrincipal currentUser) {
        try {
            com.trailiva.data.model.User userProfile = userService.getUserProfile(currentUser.getId());
            return new ResponseEntity<>(userProfile, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/profile/upload")
    public ResponseEntity<?> uploadProfileData(@CurrentUser UserPrincipal currentUser, @RequestBody ImageRequest imageProperties) {
        try {
            userService.saveImageProperties(imageProperties, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "profile image is successfully uploaded", HttpStatus.OK));
        } catch (UserException | IOException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file, @CurrentUser UserPrincipal currentUser) {
        try {
            String url = cloudinaryService.uploadImage(file, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "file is successfully uploaded", url));
        } catch (IOException | UserException exception) {
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/profile/delete")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteUploadedImage(@CurrentUser UserPrincipal currentUser, @RequestParam("public_id") String publicId) {
        try {
            cloudinaryService.deleteImage(publicId, currentUser.getId());
            return ResponseEntity.ok(new ApiResponse(true, "image deleted successfully", HttpStatus.OK));
        } catch (IOException | UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteAUser(@RequestParam("email") String email) {
        try {
            userService.deleteAUser(email);
            return ResponseEntity.ok(new ApiResponse(true, "user deleted successfully", HttpStatus.OK));
        } catch (UserException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/update")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> updatePassword(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody PasswordRequest request) {
        try {
            userService.updatePassword(request, currentUser.getEmail());
            return new ResponseEntity<>(new ApiResponse(true, "Password updated successful", HttpStatus.OK), HttpStatus.OK);
        } catch (AuthException e) {

            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updatePassword(@RequestParam Map<String, String> params,
                                            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        try {
            Map<String, Object> response = userService.SearchUserByName(params, page, size);
            return new ResponseEntity<>(new ApiResponse(true, "Data successfully filtered", response), HttpStatus.OK);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}