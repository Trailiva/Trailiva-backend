package com.trailiva.web.controller;

import com.trailiva.data.model.Token;
import com.trailiva.data.model.User;
import com.trailiva.service.AuthService;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.RoleNotFoundException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.TokenRefreshRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.ApiResponse;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static com.trailiva.data.model.TokenType.VERIFICATION;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("api/v1/trailiva/auth")
public class AuthController {

    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;


    public AuthController(AuthService authService,
                          ApplicationEventPublisher eventPublisher,
                          ModelMapper modelMapper) {
        this.authService = authService;
        this.eventPublisher = eventPublisher;
        this.modelMapper = modelMapper;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest, HttpServletRequest request) {
        try {
            User user = authService.registerNewUserAccount(userRequest);
            String token = UUID.randomUUID().toString();
            Token vToken = authService.createVerificationToken(user, token, VERIFICATION.toString());

            ResponseEntity<?> methodLinkBuilder = methodOn(AuthController.class)
                    .verifyUser(vToken.getToken());

            Link verificationLink = linkTo(methodLinkBuilder).withRel("user-verification");

            user.add(verificationLink);

            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (AuthException | MessagingException | UnsupportedEncodingException | RoleNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        JwtTokenResponse authenticationDetail = authService.login(loginRequest);
        return new ResponseEntity<>(authenticationDetail, HttpStatus.OK);
    }

    @PostMapping("/password/reset-token")
    public ResponseEntity<?> getResetPassword(@RequestParam("email") String email) {
        try {
            TokenResponse passwordResetToken = authService.createPasswordResetTokenForUser(email);
            return new ResponseEntity<>(passwordResetToken, HttpStatus.CREATED);
        } catch (AuthException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody PasswordRequest passwordRequest) {
        try {
            authService.saveResetPassword(passwordRequest);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User password is successfully updated"), HttpStatus.OK);
        } catch (AuthException | TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        try {
            authService.confirmVerificationToken(token);
            return new ResponseEntity<>(new ApiResponse
                    (true, "User is successfully verified"), HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verification/resend-token")
    public ResponseEntity<?> resendVerificationToken(@RequestParam("token") String token) {
        try {
            Token vToken = authService.resendVerificationToken(token);
            ResponseEntity<?> methodLinkBuilder = methodOn(AuthController.class)
                    .verifyUser(vToken.getToken());

            Link verificationLink = linkTo(methodLinkBuilder).withRel("user-verification");

            vToken.add(verificationLink);

            return new ResponseEntity<>(vToken, HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("password/reset/resend-token")
    public ResponseEntity<?> resendResetPasswordToken(@RequestParam("token") String token) {
        try {
            Token vToken = authService.resendResetPasswordToken(token);
            PasswordRequest passwordRequest = new PasswordRequest();
            passwordRequest.setToken(vToken.getToken());

            ResponseEntity<?> methodLinkBuilder = methodOn(AuthController.class)
                    .updatePassword(passwordRequest);

            Link verificationLink = linkTo(methodLinkBuilder).withRel("password-token");

            vToken.add(verificationLink);

            return new ResponseEntity<>(new ApiResponse
                    (true, "A new reset password token is successfully sent to your email address"), HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse
                    (false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request, HttpServletRequest httpServletRequest) {
        try {
            JwtTokenResponse jwtTokenResponse = authService.refreshToken(request);
            return new ResponseEntity<>(jwtTokenResponse, HttpStatus.OK);
        } catch (TokenException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
