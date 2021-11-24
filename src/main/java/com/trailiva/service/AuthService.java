package com.trailiva.service;

import com.trailiva.web.payload.request.LoginRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.request.ResetPasswordRequest;
import com.trailiva.web.payload.request.UserRequest;
import com.trailiva.web.payload.response.JwtTokenResponse;
import com.trailiva.web.payload.response.TokenResponse;

public interface AuthService {
    void register(UserRequest userRequest);
    JwtTokenResponse login(LoginRequest loginRequest);
     void updatePassword(PasswordRequest passwordRequest);
    void  resetPassword(ResetPasswordRequest resetPasswordRequest);
    TokenResponse generatePasswordResetToken();
}
