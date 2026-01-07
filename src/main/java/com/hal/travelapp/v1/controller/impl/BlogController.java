package com.hal.travelapp.v1.controller.impl;

import com.hal.travelapp.v1.controller.BlogApi;
import com.hal.travelapp.v1.dto.ApiSuccess;
import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.*;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.BlogService;
import com.hal.travelapp.v1.utils.SecurityContextUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlogController implements BlogApi {

    private final BlogService blogService;
    private final UserRepo userRepo;

    public BlogController(BlogService blogService, UserRepo userRepo) {
        this.blogService = blogService;
        this.userRepo = userRepo;
    }

    @Override
    public ResponseEntity<ApiSuccess<BlogDto>> createBlog(@RequestBody @Valid BlogCreateRequestDto request) {
        Long authorId = SecurityContextUtil.getCurrentUserId(userRepo);
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
    public ResponseEntity<ApiSuccess<BlogDto>> getBlogById(@PathVariable Long id) {
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
        public ResponseEntity<ApiSuccess<PageResult<BlogDto>>> getAllBlogs(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
        ) {

        PageResult<BlogDto> blogs = blogService.getAllBlogs(pageable);

        ApiSuccess<PageResult<BlogDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOGS_RETRIEVED",
                "Blogs retrieved successfully",
                blogs
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<BlogDto>> updateBlog(@PathVariable Long id, @RequestBody @Valid BlogUpdateRequestDto request) {
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
    public ResponseEntity<ApiSuccess<Void>> deleteBlog(@PathVariable Long id) {
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
    public ResponseEntity<ApiSuccess<List<BlogDto>>> getBlogsByAuthor(@PathVariable Long authorId) {
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
    public ResponseEntity<ApiSuccess<PageResult<BlogDto>>> getApprovedBlogs(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PageResult<BlogDto> blogs = blogService.getApprovedBlogs(pageable);

        ApiSuccess<PageResult<BlogDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOGS_RETRIEVED",
                "Approved blogs retrieved successfully",
                blogs
        );

        return ResponseEntity.ok(body);
    }
}

