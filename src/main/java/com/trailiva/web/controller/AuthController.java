package com.trailiva.web.controller;

import com.trailiva.data.model.Token;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.TokenRepository;
import com.trailiva.event.OnRegistrationCompleteEvent;
import com.trailiva.security.CustomUserDetailService;
import com.trailiva.security.JwtTokenProvider;
import com.trailiva.security.UserPrincipal;
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
import com.trailiva.web.payload.response.UserProfile;
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

import static com.trailiva.data.model.TokenType.REFRESH_TOKEN;
import static com.trailiva.util.Helper.isValidToken;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("api/v1/trailiva/auth")
public class AuthController {

    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;
    private final ModelMapper modelMapper;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService customUserDetailService;


    public AuthController(AuthService authService,
                          ApplicationEventPublisher eventPublisher,
                          ModelMapper modelMapper, TokenRepository tokenRepository,
                          JwtTokenProvider jwtTokenProvider, CustomUserDetailService customUserDetailService) {
        this.authService = authService;
        this.eventPublisher = eventPublisher;
        this.modelMapper = modelMapper;
        this.tokenRepository = tokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailService = customUserDetailService;
    }


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
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email) {
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

    @PostMapping("/verification/resend-token")
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

    @PostMapping("/reset-password/resend-token")
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

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        try {
            return tokenRepository.findByTokenAndTokenType(requestRefreshToken, REFRESH_TOKEN.toString())
                    .map(token -> isValidToken(token.getExpiryDate()) ? null : token)
                    .map(Token::getUser)
                    .map(user -> {
                        String jwtToken = jwtTokenProvider.generateToken((UserPrincipal) customUserDetailService.loadUserByUsername(user.getEmail()));
                        return ResponseEntity.ok(new JwtTokenResponse(jwtToken, requestRefreshToken, user.getEmail()));
                    })
                    .orElseThrow(() -> new TokenException(requestRefreshToken + " Refresh token is not in database!"));
        } catch (TokenException e) {
            e.printStackTrace();
        }
    }
}
