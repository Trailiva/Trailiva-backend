package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.request.ImageRequest;
import com.trailiva.web.payload.response.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final  ModelMapper modelMapper;


    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, ModelMapper modelMapper1) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper1;
    }

    @Override
    public User getUserProfile(Long userId) throws UserException {
        return getAUser(userId);
    }

    private User getAUser(Long userId) throws UserException {
        return userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
    }

    @Override
    public UserProfile getUserDetails(Long userId) throws UserException {
        User user = getAUser(userId);
        return modelMapper.map(user, UserProfile.class);
    }

    @Override
    public void saveImageProperties(ImageRequest imageProperties, Long userId) throws UserException {
        User user = getAUser(userId);
        user.setImageUrl(imageProperties.getUrl());
        user.setPublicId(imageProperties.getPublicId());
        saveAUser(user);
    }

    private User saveAUser(User user) {
        return userRepository.save(user);
    }
}
