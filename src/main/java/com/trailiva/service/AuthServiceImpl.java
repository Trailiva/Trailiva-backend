package com.trailiva.service;

import com.trailiva.data.model.Token;
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
import java.util.Optional;
import java.util.UUID;

import static com.trailiva.data.model.TokenType.*;
import static com.trailiva.util.Helper.isNullOrEmpty;
import static com.trailiva.util.Helper.isValidToken;


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


    private final RoleRepository roleRepository;

    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public User registerNewUserAccount(UserRequest userRequest) throws AuthException {
        if (validateEmail(userRequest.getEmail())) {
            throw new AuthException("Email is already in use");
        }
        User User = modelMapper.map(userRequest, User.class);
        User.setPassword(passwordEncoder.encode(User.getPassword()));
        User.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));
        return saveAUser(User);
    }

//    public void sendVerificationToken(User savedUser, String token) {
//        EmailRequest emailRequest = modelMapper.map(savedUser, EmailRequest.class);
//        emailRequest.setVerificationToken(token);
//        emailService.sendUserVerificationEmail(emailRequest);
//    }

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
        final String jwtToken = jwtTokenProvider.generateToken(userDetails);
        User User = internalFindUserByEmail(loginRequest.getEmail());
        Token refreshToken = new Token(User);
        tokenRepository.save(refreshToken);
        return new JwtTokenResponse(jwtToken, refreshToken.getToken(), User.getEmail());
    }

    private User internalFindUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User saveAUser(User User) {
        return userRepository.save(User);
    }

    @Override
    public void confirmVerificationToken(String verificationToken) throws TokenException {
        Token vToken = getToken(verificationToken, VERIFICATION.toString());

        if (isValidToken(vToken.getExpiryDate()))
            throw new TokenException("Token has expired");

        User User = vToken.getUser();
        User.setEnabled(true);
        saveAUser(User);
        tokenRepository.delete(vToken);
    }

    @Override
    public Token resendVerificationToken(String token) throws TokenException {
        return generateNewToken(token, VERIFICATION.toString());
    }

    @Override
    public Token resendResetPasswordToken(String verificationToken) throws TokenException {
        return generateNewToken(verificationToken, PASSWORD_RESET.toString());
    }


    @Override
    public Token createVerificationToken(User User, String token, String tokenType) {
        Token verificationToken = new Token(token, User, tokenType);
        return tokenRepository.save(verificationToken);
    }


    private Token generateNewToken(String token, String tokenType) throws TokenException {
        Token vCode = getToken(token, tokenType);
        vCode.updateToken(UUID.randomUUID().toString(), tokenType);
        return tokenRepository.save(vCode);
    }

    @Override
    public TokenResponse createPasswordResetTokenForUser(String email) throws AuthException {
        User User = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with email " + email));
        String token = UUID.randomUUID().toString();
        Token createdToken = createVerificationToken(User, token, PASSWORD_RESET.toString());
        return modelMapper.map(createdToken, TokenResponse.class);
    }

    @Override
    public JwtTokenResponse refreshToken(TokenRefreshRequest request) throws TokenException {
        String requestRefreshToken = request.getRefreshToken();
        Optional<Token> refreshToken = tokenRepository.findByTokenAndTokenType(requestRefreshToken, REFRESH.toString());
        if (refreshToken.isPresent()) {
            Token token = getRefreshToken(refreshToken.get());
            String jwtToken = jwtTokenProvider.generateToken((UserPrincipal)
                    customUserDetailService.loadUserByUsername(token.getUser().getEmail()));
            return new JwtTokenResponse(jwtToken, requestRefreshToken, token.getUser().getEmail());
        } else throw new TokenException("Invalid refresh token");
    }

    private Token getRefreshToken(Token token) throws TokenException {
        if (!isValidToken(token.getExpiryDate()))
            return token;
        else throw new TokenException("Refresh token was expired. Please make a new sign in request");
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
        tokenRepository.delete(pToken);
    }

}
