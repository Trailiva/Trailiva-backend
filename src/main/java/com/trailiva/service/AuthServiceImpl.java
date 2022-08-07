package com.trailiva.service;

import com.trailiva.data.model.Token;
import com.trailiva.data.model.TokenType;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.RoleRepository;
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
import java.util.List;
import java.util.UUID;

import static com.trailiva.data.model.TokenType.PASSWORD_RESET;
import static com.trailiva.data.model.TokenType.VERIFICATION;
import static com.trailiva.util.Helper.isNullOrEmpty;


@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailService customUserDetailService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    private final EmailService emailService;

    private final RoleRepository roleRepository;

    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public User registerNewUserAccount(UserRequest userRequest) throws AuthException {
        if (validateEmail(userRequest.getEmail())) {
            throw new AuthException("Email is already in use");
        }
        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        return saveAUser(user);
    }

    public void sendVerificationToken(User savedUser) {
        EmailRequest emailRequest = modelMapper.map(savedUser, EmailRequest.class);
        emailService.sendUserVerificationEmail(emailRequest);
    }

    @Transactional
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
        com.trailiva.data.model.User user = internalFindUserByEmail(loginRequest.getEmail());
        return new JwtTokenResponse(token, user.getEmail());
    }

    private User internalFindUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User saveAUser(com.trailiva.data.model.User user) {
        return userRepository.save(user);
    }

    private boolean isValidToken(Token vCode) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), vCode.getExpiryDate());
        return minutes <= 0;
    }

    @Override
    public void confirmVerificationToken(String verificationToken) throws TokenException {
        Token vToken = getToken(verificationToken, VERIFICATION.toString());

        if (isValidToken(vToken))
            throw new TokenException("Token has expired");

        User user = vToken.getUser();
        user.setEnabled(true);
        saveAUser(user);
        tokenRepository.delete(vToken);
    }

    @Override
    public void resendVerificationToken(String verificationToken) throws TokenException {
        Token token = generateNewToken(verificationToken, VERIFICATION.toString());
        sendVerificationToken(token.getUser());
    }

    @Override
    public void resendResetPasswordToken(String verificationToken) throws TokenException {
        Token token = generateNewToken(verificationToken, PASSWORD_RESET.toString());
    }


    @Override
    public Token createVerificationToken(User user, String token, String tokenType) {
        Token verificationToken = new Token(token, user, tokenType);
        return tokenRepository.save(verificationToken);
    }


    private Token generateNewToken(String token, String tokenType) throws TokenException {
        Token vCode = getToken(token, tokenType);
        vCode.updateToken(UUID.randomUUID().toString(), tokenType);
        return tokenRepository.save(vCode);
    }

    @Override
    public TokenResponse createPasswordResetTokenForUser(String email) throws AuthException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with email " + email));
        String token = UUID.randomUUID().toString();
        Token createdToken = createVerificationToken(user, token, PASSWORD_RESET.toString());
        return modelMapper.map(createdToken, TokenResponse.class);
    }

    @Override
    public boolean validatePasswordResetToken(String token) throws TokenException {
        final Token passToken = getToken(token, PASSWORD_RESET.toString());
        if (isValidToken(passToken)) throw new TokenException("Token has expired");
        return true;
    }


    private Token getToken(String token, String tokenType) throws TokenException {
        return tokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new TokenException("Invalid token"));
    }

    @Override
    public void saveResetPassword(PasswordRequest request) throws TokenException, AuthException {
        if (isNullOrEmpty(request.getToken())) throw new AuthException("Password must cannot be blank");
        Token pToken = getToken(request.getToken(), PASSWORD_RESET.toString());
        User userToChangePassword = pToken.getUser();
        userToChangePassword.setPassword(passwordEncoder.encode(request.getPassword()));
        saveAUser(userToChangePassword);
    }

}
