package com.trailiva.web.controller;

import com.trailiva.data.model.User;
import com.trailiva.event.OnRegistrationCompleteEvent;
import com.trailiva.service.AuthService;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.RoleNotFoundException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserVerificationException;
import com.trailiva.web.payload.request.ForgetPasswordRequest;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.ApiResponse;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;
import com.trailiva.web.payload.response.UserProfile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("api/v1/trailiva/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest, HttpServletRequest request) {
        try {
            User user = authService.registerNewUserAccount(userRequest);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
            return new ResponseEntity<>(modelMapper.map(user, UserProfile.class), HttpStatus.CREATED);
        } catch (AuthException | MessagingException | UnsupportedEncodingException | RoleNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtTokenResponse authenticationDetail = authService.login(loginRequest);
        return new ResponseEntity<>(authenticationDetail, HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        try {
            authService.resetPassword(passwordRequest);
            return new ResponseEntity<>(new ApiResponse<>(true, "User password is successfully updated", HttpStatus.OK), HttpStatus.OK);
        } catch (AuthException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/password/token/{email}")
    public ResponseEntity<?> getForgetPasswordToken(@Valid @PathVariable String email) {
        try {
            TokenResponse passwordResetToken = authService.generatePasswordResetToken(email);
            return new ResponseEntity<>(passwordResetToken, HttpStatus.CREATED);
        } catch (AuthException exception) {
            return new ResponseEntity<>(new ApiResponse<>(false, exception.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/forget-password/{token}")
    public ResponseEntity<?> forgetPassword(@Valid @PathVariable String token, @RequestBody ForgetPasswordRequest request) {
        try {
            authService.forgetPassword(request, token);
            return new ResponseEntity<>(new ApiResponse<>(true, "Password reset is successful", HttpStatus.OK), HttpStatus.OK);
        } catch (AuthException | TokenException exception) {
            return new ResponseEntity<>(new ApiResponse<>(false, exception.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        try {
            authService.comfirmVerificationToken(token);
            return new ResponseEntity<>(new ApiResponse<>(true, "User is successfully verified", HttpStatus.OK), HttpStatus.OK);
        } catch (UserVerificationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/resend-token")
    public ResponseEntity<?> resendToken(@RequestParam("token") String token) {
        try {
            authService.resendVerificationToken(token);
            return new ResponseEntity<>(new ApiResponse<>(true, "Verification token is successfully sent to your email address", HttpStatus.OK), HttpStatus.OK);
        } catch (UserVerificationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
