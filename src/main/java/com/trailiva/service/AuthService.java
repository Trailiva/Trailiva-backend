package com.trailiva.service;

import com.trailiva.web.exceptions.*;
import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.ForgetPasswordRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;
import com.trailiva.web.payload.response.UserProfile;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    UserProfile registerNewUserAccount(UserRequest userRequest, String siteUrl) throws AuthException, MessagingException, UnsupportedEncodingException, RoleNotFoundException;
    JwtTokenResponse login(LoginRequest loginRequest);
    void resetPassword(PasswordRequest passwordRequest) throws AuthException;
    void forgetPassword(ForgetPasswordRequest forgetPasswordRequest, String passwordResetToken) throws AuthException, TokenException;
    TokenResponse generatePasswordResetToken(String email) throws AuthException;
    void verify(String verificationToken) throws UserVerificationException;
    void resendVerificationToken(String email) throws UserException;

}
