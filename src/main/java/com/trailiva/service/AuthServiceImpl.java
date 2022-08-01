package com.trailiva.service;

import com.trailiva.data.model.Token;
import com.trailiva.data.model.TokenType;
import com.trailiva.data.model.User;
import com.trailiva.data.model.VerificationCode;
import com.trailiva.data.repository.RoleRepository;
import com.trailiva.data.repository.TokenRepository;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.data.repository.VerificationCodeRepository;
import com.trailiva.security.CustomUserDetailService;
import com.trailiva.security.JwtTokenProvider;
import com.trailiva.security.UserPrincipal;
import com.trailiva.web.exceptions.*;
import com.trailiva.web.payload.request.*;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailService customUserDetailService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final TokenRepository tokenRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final EmailService emailService;

    private final RoleRepository roleRepository;

    private final VerificationCodeRepository verificationCodeRepository;

    @Override
    @Transactional
    public User registerNewUserAccount(UserRequest userRequest) throws AuthException {
        if (validateEmail(userRequest.getEmail())) {
            throw new AuthException("Email is already in use");
        }
        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER")));
        return saveAUser(user);
    }

    public void sendVerificationToken(User savedUser) {
        EmailRequest emailRequest = modelMapper.map(savedUser, EmailRequest.class);
        emailService.sendUserVerificationEmail(emailRequest);
    }


    @Override
    @Transactional
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
        com.trailiva.data.model.User user = internalFindUserByEmail(loginRequest.getEmail());
        return new JwtTokenResponse(token, user.getEmail());
    }

    private com.trailiva.data.model.User internalFindUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void resetPassword(PasswordRequest request) throws AuthException {
        String email = request.getEmail();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getPassword();
        com.trailiva.data.model.User userToChangePassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with email" + email));

        boolean passwordMatch = passwordEncoder.matches(oldPassword, userToChangePassword.getPassword());
        if (!passwordMatch) {
            throw new AuthException("Passwords do not match");
        }
        userToChangePassword.setPassword(passwordEncoder.encode(newPassword));
        saveAUser(userToChangePassword);
    }

    @Override
    public void forgetPassword(ForgetPasswordRequest request, String passwordResetToken) throws AuthException, TokenException {
        String email = request.getEmail();
        String newPassword = request.getPassword();
        com.trailiva.data.model.User userToResetPassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with user name " + email));
        Token token = tokenRepository.findByToken(passwordResetToken)
                .orElseThrow(() -> new TokenException(format("No token with value %s found", passwordResetToken)));
        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenException("This password reset token has expired ");
        }
        if (!token.getUserId().equals(userToResetPassword.getUserId())) {
            throw new TokenException("This password rest token does not belong to this user");
        }
        userToResetPassword.setPassword(passwordEncoder.encode(newPassword));
        saveAUser(userToResetPassword);
        tokenRepository.delete(token);
    }

    @Override
    public TokenResponse generatePasswordResetToken(String email) throws AuthException {
        com.trailiva.data.model.User userToResetPassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with user name " + email));
        Token token = new Token();
        token.setType(TokenType.PASSWORD_RESET);
        token.setUserId(userToResetPassword.getUserId());
        token.setToken(UUID.randomUUID().toString());
        token.setExpiry(LocalDateTime.now().plusMinutes(30));
        return modelMapper.map(tokenRepository.save(token), TokenResponse.class);
    }

    private boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User saveAUser(com.trailiva.data.model.User user) {
        return userRepository.save(user);
    }

    private boolean isValidVerificationToken(VerificationCode vCode) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), vCode.getExpiryDate());
        return minutes <= 0;
    }

    @Override
    public void comfirmVerificationToken(String verificationToken) throws UserVerificationException {
        VerificationCode vCode = getVerificationToken(verificationToken);

        if (isValidVerificationToken(vCode))
            throw new UserVerificationException("Token has expired");

        User user = vCode.getUser();
        user.setEnabled(true);
        saveAUser(user);
        verificationCodeRepository.delete(vCode);
    }

    @Override
    public void resendVerificationToken(String verificationToken) throws UserVerificationException {
        VerificationCode verificationCode = generateNewVerificationToken(verificationToken);
        sendVerificationToken(verificationCode.getUser());
    }


    @Override
    public void createVerificationToken(User user, String token) {
        VerificationCode verificationCode = new VerificationCode(token, user);
        verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode generateNewVerificationToken(String verificationCode) throws UserVerificationException {
        VerificationCode vCode = getVerificationToken(verificationCode);
        vCode.updateToken(UUID.randomUUID().toString());
        return verificationCodeRepository.save(vCode);
    }

    private VerificationCode getVerificationToken(String verificationCode) throws UserVerificationException {
        return verificationCodeRepository.findByCode(verificationCode)
                .orElseThrow(() -> new UserVerificationException("Invalid token"));
    }
}
