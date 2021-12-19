package com.trailiva.web.controller;

import com.trailiva.data.model.Token;
import com.trailiva.service.AuthService;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.payload.request.*;
import com.trailiva.web.payload.response.ApiResponse;
import com.trailiva.web.payload.response.JwtTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/trailiva/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest) {
        try {
            authService.register(userRequest);
            return new ResponseEntity<>(new ApiResponse(true, "User successfully created"), HttpStatus.CREATED);
        } catch (AuthException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody  LoginRequest loginRequest) {
        JwtTokenResponse authenticationDetail = authService.login(loginRequest);
        return new ResponseEntity<>(authenticationDetail, HttpStatus.OK);
    }

    @PostMapping("/password/update")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody  PasswordRequest passwordRequest){
        try {
            authService.updatePassword(passwordRequest);
            return new ResponseEntity<>(new ApiResponse(true, "User password is successfully updated"), HttpStatus.OK);
        }catch (AuthException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/password/reset/{username}")
    public ResponseEntity<?> forgotPassword(@Valid @PathVariable String username){
        try {
            Token passwordResetToken = authService.generatePasswordResetToken(username);
            return new ResponseEntity<>(passwordResetToken, HttpStatus.CREATED  );
        }catch (AuthException exception){
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/password/reset/{token}")
    public ResponseEntity<?> resetPassword( @Valid  @PathVariable String token, @RequestBody ResetPasswordRequest request){
        try{
            authService.resetPassword(request, token);
            return new ResponseEntity<>(new ApiResponse(true, "Password reset is successful"), HttpStatus.OK);
        }catch (AuthException | TokenException exception){
            return new ResponseEntity<>(new ApiResponse(false, exception.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}

