package com.trailiva.service;

import com.trailiva.web.exceptions.UserException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadImage(MultipartFile file, Long userId) throws IOException, UserException;
}
