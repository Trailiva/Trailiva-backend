package com.trailiva.service;

import com.trailiva.data.model.User;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.specification.UserSpecifications;
import com.trailiva.util.Helper;
import com.trailiva.web.exceptions.AuthException;
import com.trailiva.web.exceptions.BadRequestException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.payload.request.ImageRequest;
import com.trailiva.web.payload.request.PasswordRequest;
import com.trailiva.web.payload.response.UserProfile;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.trailiva.util.Helper.isNullOrEmpty;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserProfile(Long userId) throws UserException {
        return getAUser(userId);
    }

    private User getAUser(Long userId) throws UserException {
        return userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
    }

    @Override
    public UserProfile getUserDetails(Long userId) throws UserException {
        User User = getAUser(userId);
        return modelMapper.map(User, UserProfile.class);
    }

    @Override
    public void saveImageProperties(ImageRequest imageProperties, Long userId) throws UserException, IOException {
        User User = getAUser(userId);
        String url = User.getImageUrl();
        String publicId = User.getPublicId();

        if (!Objects.isNull(url) && !Objects.isNull(publicId))
            cloudinaryService.deleteImage(publicId, userId);

        User.setImageUrl(imageProperties.getUrl());
        User.setPublicId(imageProperties.getPublicId());
        saveAUser(User);
    }

    @Override
    public void deleteAUser(String email) throws UserException {
        User User = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found with email " + email));
        userRepository.delete(User);
    }

    private User saveAUser(User User) {
        return userRepository.save(User);
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

    @Override
    public Map<String, Object> SearchUserByName(Map<String, String> params, int page, int size) throws BadRequestException {
        Helper.validatePageNumberAndSize(page, size);
        Specification<User> withFirstName = UserSpecifications.withFirstName(params.get("firstName"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "firstName"));
        Page<User> result = userRepository.findAll(
                Specification.where(withFirstName),
                pageable
        );
        Map<String, Object> response = new HashMap<>();
        response.put("data", result.getContent());
        response.put("recordsTotal", result.getTotalElements());
        response.put("recordsFiltered", result.getTotalElements());
        return response;
    }

}
