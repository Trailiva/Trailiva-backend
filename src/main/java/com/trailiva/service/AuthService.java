package com.trailiva.service;

import com.trailiva.data.model.Token;
import com.trailiva.data.model.User;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.RoleNotFoundException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.TokenRefreshRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    User registerNewUserAccount(UserRequest userRequest) throws AuthException, MessagingException, UnsupportedEncodingException, RoleNotFoundException;

    JwtTokenResponse login(LoginRequest loginRequest);

    void saveResetPassword(PasswordRequest passwordRequest) throws AuthException, TokenException;

    void confirmVerificationToken(String verificationToken) throws TokenException;

    Token resendVerificationToken(String token) throws TokenException;

    Token resendResetPasswordToken(String token) throws TokenException;

    Token createVerificationToken(User User, String token, String tokenType);

//    void sendVerificationToken(User User, String token);

    TokenResponse createPasswordResetTokenForUser(String email) throws AuthException;

    JwtTokenResponse refreshToken(TokenRefreshRequest request) throws TokenException;
}
