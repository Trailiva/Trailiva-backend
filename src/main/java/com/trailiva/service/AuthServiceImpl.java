package com.trailiva.service;

import com.trailiva.data.model.Token;
import com.trailiva.data.model.TokenType;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.TokenRepository;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.security.CustomUserDetailService;
import com.trailiva.security.JwtTokenProvider;
import com.trailiva.security.UserPrincipal;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.payload.request.*;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl() {
    }

    @Override
    public User register(UserRequest userRequest) throws AuthException {
        if (validateEmail(userRequest.getEmail())) {
            throw new AuthException("Email is already in use");
        }
        User user = modelMapper.map(userRequest, User.class);
        return userRepository.save(user);
//        String otp = otpService.generateOtp(userDto.getEmail());
//        try {
//            emailNotificationService.sendEmailTo(user.getEmail(), "OTP Semicolon ORM", String.format("Your OTP is %s", otp));
//            return save(user);
//        } catch (UnirestException | URISyntaxException exception) {
//            log.info("Exception --> {}", exception.getMessage());
//            throw new AuthException(
//                    String.format("Error sending email verification message to %s", user.getEmail()));
//        }

    }

    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserPrincipal userDetails = (UserPrincipal) customUserDetailService.loadUserByUsername(loginRequest.getEmail());
        final String token = jwtTokenProvider.generateToken(userDetails);
        User user = internalFindUserByEmail(loginRequest.getEmail());
        return new JwtTokenResponse(token, user.getEmail());
    }

    private User internalFindUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void updatePassword(PasswordRequest request) throws AuthException {
        String email = request.getEmail();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getPassword();
        User userToChangePassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with email" + email));

        boolean passwordMatch = passwordEncoder.matches(oldPassword, userToChangePassword.getPassword());
        if (!passwordMatch) {
            throw new AuthException("Passwords do not match");
        }
        userToChangePassword.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userToChangePassword);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request, String passwordResetToken) throws AuthException, TokenException {
        String email = request.getEmail();
        String newPassword = request.getPassword();
        User userToResetPassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with user name " + email));
        Token token = tokenRepository.findByToken(passwordResetToken)
                .orElseThrow(() -> new TokenException(String.format("No token with value %s found", passwordResetToken)));
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenException("This password reset token has expired ");
        }
        if (!token.getUser().getId().equals(userToResetPassword.getId())) {
            throw new TokenException("This password rest token does not belong to this user");
        }
        userToResetPassword.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userToResetPassword);
        tokenRepository.delete(token);
    }

    @Override
    public Token generatePasswordResetToken(String email) throws AuthException {
        User userToResetPassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with user name " + email));
        Token token = new Token();
        token.setType(TokenType.PASSWORD_RESET);
        token.setUser(userToResetPassword);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiry(LocalDateTime.now().plusMinutes(30));
        return tokenRepository.save(token);
    }

    private boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User save(User user) {
        return userRepository.save(user);
    }
}
