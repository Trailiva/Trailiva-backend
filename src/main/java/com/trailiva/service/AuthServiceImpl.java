package com.trailiva.service;

import com.trailiva.data.repository.TokenRepository;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.security.CustomUserDetailService;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.ResetPasswordRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void register(UserRequest userRequest) {

    }

    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) {
        return null;
    }

    @Override
    public void updatePassword(PasswordRequest passwordRequest) {

    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {

    }

    @Override
    public TokenResponse generatePasswordResetToken() {
        return null;
    }
}
