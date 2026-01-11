package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubImageUploadServiceImpl implements ImageUploadService {

    private final RestTemplate restTemplate;
    
    @Value("${github.upload.repo-owner:}")
    private String repoOwner;
    
    @Value("${github.upload.repo-name:}")
    private String repoName;
    
    @Value("${github.upload.branch:main}")
    private String branch;
    
    @Value("${github.upload.token:}")
    private String githubToken;
    
    @Value("${github.upload.path:images}")
    private String uploadPath;
    
    @Value("${github.upload.base-url:https://raw.githubusercontent.com}")
    private String baseUrl;

    @Override
    public String uploadImage(MultipartFile file, String fileName) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File cannot be null or empty");
            }
            
            // Validate image type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File must be an image");
            }
            
            // Generate file name if not provided
            if (fileName == null || fileName.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                } else {
                    // Determine extension from content type
                    extension = switch (contentType) {
                        case "image/jpeg", "image/jpg" -> ".jpg";
                        case "image/png" -> ".png";
                        case "image/gif" -> ".gif";
                        case "image/webp" -> ".webp";
                        default -> ".jpg";
                    };
                }
                fileName = generateUniqueFileName(extension);
            }
            
            // Ensure fileName has extension
            if (!fileName.contains(".")) {
                String extension = switch (contentType) {
                    case "image/jpeg", "image/jpg" -> ".jpg";
                    case "image/png" -> ".png";
                    case "image/gif" -> ".gif";
                    case "image/webp" -> ".webp";
                    default -> ".jpg";
                };
                fileName = fileName + extension;
            }
            
            // Read file content and encode to base64
            byte[] fileContent = file.getBytes();
            String base64Content = Base64.getEncoder().encodeToString(fileContent);
            
            // Construct file path in repository
            String filePath = uploadPath + "/" + fileName;
            
            // Prepare GitHub API request
            String apiUrl = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                repoOwner, repoName, filePath
            );
            
            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", "Upload image: " + fileName);
            requestBody.put("content", base64Content);
            requestBody.put("branch", branch);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Make API call
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.PUT,
                request,
                Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // Construct public URL
                String publicUrl = String.format(
                    "%s/%s/%s/%s/%s",
                    baseUrl, repoOwner, repoName, branch, filePath
                );
                log.info("Successfully uploaded image to GitHub: {}", publicUrl);
                return publicUrl;
            } else {
                throw new RuntimeException("Failed to upload image to GitHub. Status: " + response.getStatusCode());
            }
            
        } catch (IOException e) {
            log.error("Error reading file content", e);
            throw new RuntimeException("Failed to read file content", e);
        } catch (Exception e) {
            log.error("Error uploading image to GitHub", e);
            throw new RuntimeException("Failed to upload image to GitHub: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            // Extract file path from URL
            // URL format: https://raw.githubusercontent.com/owner/repo/branch/path/to/file
            String filePath = extractFilePathFromUrl(imageUrl);
            
            if (filePath == null) {
                throw new IllegalArgumentException("Invalid image URL format");
            }
            
            // Get file SHA (required for deletion)
            String apiUrl = String.format(
                "https://api.github.com/repos/%s/%s/contents/%s",
                repoOwner, repoName, filePath
            );
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            
            HttpEntity<Void> getRequest = new HttpEntity<>(headers);
            
            // First, get the file to retrieve its SHA
            ResponseEntity<Map> getResponse = restTemplate.exchange(
                apiUrl + "?ref=" + branch,
                HttpMethod.GET,
                getRequest,
                Map.class
            );
            
            if (!getResponse.getStatusCode().is2xxSuccessful()) {
                log.warn("File not found in GitHub, may already be deleted: {}", imageUrl);
                return;
            }
            
            Map<String, Object> fileInfo = getResponse.getBody();
            if (fileInfo == null || !fileInfo.containsKey("sha")) {
                throw new RuntimeException("Could not retrieve file SHA from GitHub");
            }
            
            String sha = (String) fileInfo.get("sha");
            
            // Delete the file
            Map<String, Object> deleteBody = new HashMap<>();
            deleteBody.put("message", "Delete image: " + filePath);
            deleteBody.put("sha", sha);
            deleteBody.put("branch", branch);
            
            HttpHeaders deleteHeaders = new HttpHeaders();
            deleteHeaders.setContentType(MediaType.APPLICATION_JSON);
            deleteHeaders.setBearerAuth(githubToken);
            deleteHeaders.set("Accept", "application/vnd.github.v3+json");
            
            HttpEntity<Map<String, Object>> deleteRequest = new HttpEntity<>(deleteBody, deleteHeaders);
            
            ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                apiUrl,
                HttpMethod.DELETE,
                deleteRequest,
                Void.class
            );
            
            if (deleteResponse.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully deleted image from GitHub: {}", imageUrl);
            } else {
                throw new RuntimeException("Failed to delete image from GitHub. Status: " + deleteResponse.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("Error deleting image from GitHub", e);
            throw new RuntimeException("Failed to delete image from GitHub: " + e.getMessage(), e);
        }
    }
    
    private String generateUniqueFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + extension;
    }
    
    private String extractFilePathFromUrl(String imageUrl) {
        try {
            // URL format: https://raw.githubusercontent.com/owner/repo/branch/path/to/file
            String prefix = baseUrl + "/" + repoOwner + "/" + repoName + "/" + branch + "/";
            if (imageUrl.startsWith(prefix)) {
                return imageUrl.substring(prefix.length());
            }
            // Try to extract from full URL
            if (imageUrl.contains("/" + repoName + "/" + branch + "/")) {
                int startIndex = imageUrl.indexOf("/" + repoName + "/" + branch + "/") + 
                                repoName.length() + branch.length() + 2;
                return imageUrl.substring(startIndex);
            }
            return null;
        } catch (Exception e) {
            log.error("Error extracting file path from URL: {}", imageUrl, e);
            return null;
        }
    }
}

