package com.trailiva.service;

import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.request.ImageRequest;
import com.trailiva.web.payload.response.UserProfile;

import java.io.IOException;

public interface UserService {
    com.trailiva.data.model.User getUserProfile(Long userId) throws UserException;

    UserProfile getUserDetails(Long userId) throws UserException;

    void saveImageProperties(ImageRequest imageProperties, Long userId) throws UserException, IOException;
}
