package com.trailiva.service;

import com.trailiva.data.model.Token;
import com.trailiva.data.model.User;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserVerificationException;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.ResetPasswordRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.UserResponse;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    void register(UserRequest userRequest, String siteUrl) throws AuthException, MessagingException, UnsupportedEncodingException;
    JwtTokenResponse login(LoginRequest loginRequest);
     void updatePassword(PasswordRequest passwordRequest) throws AuthException;
    void  resetPassword(ResetPasswordRequest resetPasswordRequest, String passwordResetToken) throws AuthException, TokenException;
    Token generatePasswordResetToken(String email) throws AuthException;
    void sendVerificationEmail(User user, String siteUrl) throws MessagingException, UnsupportedEncodingException;
    boolean verify(String verificationToken) throws UserVerificationException;
}
