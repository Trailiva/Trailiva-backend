package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.request.ImageRequest;
import com.trailiva.web.payload.response.UserProfile;

import java.io.IOException;

public interface UserService {
    User getUserProfile(Long userId) throws UserException;

    UserProfile getUserDetails(Long userId) throws UserException;

    void saveImageProperties(ImageRequest imageProperties, Long userId) throws UserException, IOException;
}
