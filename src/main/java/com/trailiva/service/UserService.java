package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.request.ImageRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.response.UserProfile;

import java.io.IOException;
import java.util.Map;

public interface UserService {
    User getUserProfile(Long userId) throws UserException;

    UserProfile getUserDetails(Long userId) throws UserException;
    void updatePassword(PasswordRequest updatePasswordRequest, String email) throws AuthException;

    void saveImageProperties(ImageRequest imageProperties, Long userId) throws UserException, IOException;

    void deleteAUser(String email) throws UserException;

    Map<String, Object> SearchUserByName(Map<String, String> params, int page, int size) throws BadRequestException;
}
