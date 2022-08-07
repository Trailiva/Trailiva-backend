package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.request.ImageRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.response.UserProfile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static com.trailiva.util.Helper.isNullOrEmpty;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final  ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;


    @Override
    public com.trailiva.data.model.User getUserProfile(Long userId) throws UserException {
        return getAUser(userId);
    }

    private com.trailiva.data.model.User getAUser(Long userId) throws UserException {
        return userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
    }

    @Override
    public UserProfile getUserDetails(Long userId) throws UserException {
        com.trailiva.data.model.User user = getAUser(userId);
        return modelMapper.map(user, UserProfile.class);
    }

    @Override
    public void saveImageProperties(ImageRequest imageProperties, Long userId) throws UserException, IOException {
        com.trailiva.data.model.User user = getAUser(userId);
        String url = user.getImageUrl();
        String publicId = user.getPublicId();

        if(!Objects.isNull(url) && !Objects.isNull(publicId))
            cloudinaryService.deleteImage(publicId, userId);

        user.setImageUrl(imageProperties.getUrl());
        user.setPublicId(imageProperties.getPublicId());
        saveAUser(user);
    }

    @Override
    public void deleteAUser(String email) throws UserException {
        User user =  userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found with email " + email));
        userRepository.delete(user);
    }

    private User saveAUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public void updatePassword(PasswordRequest request, String email) throws AuthException {
        if (isNullOrEmpty(request.getOldPassword())) throw new AuthException("Password must cannot be blank");

        User userToChangePassword = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("No user found with email" + email));

        boolean passwordMatch = passwordEncoder.matches(request.getOldPassword(), userToChangePassword.getPassword());
        if (!passwordMatch) {
            throw new AuthException("Passwords do not match");
        }
        userToChangePassword.setPassword(passwordEncoder.encode(request.getPassword()));
        saveAUser(userToChangePassword);
    }

}
