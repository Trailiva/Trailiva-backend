package com.trailiva.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.trailiva.data.model.User;
import com.trailiva.data.repository.UserRepository;
import com.trailiva.web.exceptions.UserException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;
    private final UserRepository userRepository;

    public CloudinaryServiceImpl(Cloudinary cloudinary, UserRepository userRepository) {
        this.cloudinary = cloudinary;
        this.userRepository = userRepository;
    }

    @Override
    public String uploadImage(MultipartFile file, Long userId) throws IOException, UserException {
        User User = userRepository.findById(userId).orElseThrow(() -> new UserException("user not found"));
        Map<?, ?> imageProperties = ObjectUtils.asMap("public_id", "user_images" + extractFileName(file.getName()));
        Map<?, ?> uploadResult =  cloudinary.uploader().upload(file.getBytes(), imageProperties);
        String imageUrl =  String.valueOf(uploadResult.get("url"));
        User.setImageUrl(imageUrl);
        userRepository.save(User);
        return  imageUrl;
    }

    @Override
    public void deleteImage(String publicId, Long userId) throws IOException, UserException {
        User User = getAUser(userId);
        Map deleteParams = ObjectUtils.asMap("invalidate", true );
        cloudinary.uploader().destroy(publicId ,deleteParams);
        User.setPublicId(null);
        User.setImageUrl(null);
        userRepository.save(User);
    }

    private String extractFileName(String name) {
        return name.split("\\.")[0];
    }

    private User getAUser(Long userId) throws UserException {
        return userRepository.findById(userId).orElseThrow(() -> new UserException("User not found"));
    }
}
