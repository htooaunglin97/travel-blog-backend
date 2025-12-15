package com.hal.travelapp.v1.controller.impl;

import com.hal.travelapp.v1.controller.BlogApi;
import com.hal.travelapp.v1.dto.*;
import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.BaseEntity;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.BlogService;
import com.hal.travelapp.v1.service.impl.BlogServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BlogController implements BlogApi {

    private final BlogService blogService;
    private final UserRepo userRepo;

    public BlogController(BlogServiceImpl blogService, UserRepo userRepo) {
        this.blogService = blogService;
        this.userRepo = userRepo;
    }

    @Override
    public ResponseEntity<ApiSuccess<BlogDto>> createBlog(@org.springframework.web.bind.annotation.RequestBody @Valid BlogCreateRequestDto request) {
        Long authorId = getCurrentUserId();
        BlogDto blogDto = blogService.createBlog(request, authorId);

        ApiSuccess<BlogDto> body = new ApiSuccess<>(
                HttpStatus.CREATED,
                "BLOG_CREATED",
                "Blog created successfully",
                blogDto
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<BlogDto>> getBlogById(@org.springframework.web.bind.annotation.PathVariable Long id) {
        BlogDto blogDto = blogService.getBlogById(id);

        ApiSuccess<BlogDto> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOG_FOUND",
                "Blog retrieved successfully",
                blogDto
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<List<BlogDto>>> getAllBlogs() {
        List<BlogDto> blogs = blogService.getAllBlogs();

        ApiSuccess<List<BlogDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOGS_RETRIEVED",
                "Blogs retrieved successfully",
                blogs
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<BlogDto>> updateBlog(@org.springframework.web.bind.annotation.PathVariable Long id, @org.springframework.web.bind.annotation.RequestBody @Valid BlogUpdateRequestDto request) {
        BlogDto blogDto = blogService.updateBlog(id, request);

        ApiSuccess<BlogDto> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOG_UPDATED",
                "Blog updated successfully",
                blogDto
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<Void>> deleteBlog(@org.springframework.web.bind.annotation.PathVariable Long id) {
        blogService.deleteBlog(id);

        ApiSuccess<Void> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOG_DELETED",
                "Blog deleted successfully",
                null
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<List<BlogDto>>> getBlogsByAuthor(@org.springframework.web.bind.annotation.PathVariable Long authorId) {
        List<BlogDto> blogs = blogService.getBlogsByAuthor(authorId);

        ApiSuccess<List<BlogDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOGS_RETRIEVED",
                "Blogs retrieved successfully",
                blogs
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<List<BlogDto>>> getApprovedBlogs() {
        List<BlogDto> blogs = blogService.getApprovedBlogs();

        ApiSuccess<List<BlogDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOGS_RETRIEVED",
                "Approved blogs retrieved successfully",
                blogs
        );

        return ResponseEntity.ok(body);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            return userRepo.findByEmail(email)
                    .map(BaseEntity::getId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        throw new RuntimeException("User not authenticated");
    }
}

