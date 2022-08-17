package com.trailiva.web.controller;

import com.trailiva.data.model.User;
import com.trailiva.event.OnRegistrationCompleteEvent;
import com.trailiva.service.AuthService;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.RoleNotFoundException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.ApiResponse;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;
import com.trailiva.web.payload.response.UserProfile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtTokenResponse authenticationDetail = authService.login(loginRequest);
        return new ResponseEntity<>(authenticationDetail, HttpStatus.OK);
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email){
        try {
            TokenResponse passwordResetToken = authService.createPasswordResetTokenForUser(email);
            return new ResponseEntity<>(passwordResetToken, HttpStatus.CREATED);
        } catch (AuthException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/password/validate-token")
    public ResponseEntity<?> validatePasswordToken(@RequestParam("token") String token) throws TokenException {
        boolean isValid = authService.validatePasswordResetToken(token);
        return new ResponseEntity<>(new ApiResponse
                (isValid, "Password token is valid", HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/password/save-reset-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        try {
            authService.saveResetPassword(passwordRequest);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User password is successfully updated", HttpStatus.OK), HttpStatus.OK);
        } catch (AuthException | TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        try {
            authService.confirmVerificationToken(token);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User is successfully verified", HttpStatus.OK), HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("verification/resend-token")
    public ResponseEntity<?> resendVerificationToken(@RequestParam("token") String token) {
        try {
            authService.resendVerificationToken(token);
            return new ResponseEntity<>(new ApiResponse
                    (true, "Verification token is successfully sent to your email address", HttpStatus.OK), HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("reset-password/resend-token")
    public ResponseEntity<?> resendResetPasswordToken(@RequestParam("token") String token) {
        try {
            authService.resendResetPasswordToken(token);
            return new ResponseEntity<>(new ApiResponse
                    (true, "Reset password token is successfully sent to your email address", HttpStatus.OK), HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
