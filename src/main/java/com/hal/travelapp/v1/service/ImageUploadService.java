package com.hal.travelapp.v1.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    /**
     * Uploads an image file to GitHub repository and returns the public URL
     * @param file The image file to upload
     * @param fileName Optional custom file name. If null, a unique name will be generated
     * @return The public URL of the uploaded image
     * @throws RuntimeException if upload fails
     */
    String uploadImage(MultipartFile file, String fileName);
    
    /**
     * Deletes an image from GitHub repository
     * @param imageUrl The URL of the image to delete
     * @throws RuntimeException if deletion fails
     */
    void deleteImage(String imageUrl);
}

