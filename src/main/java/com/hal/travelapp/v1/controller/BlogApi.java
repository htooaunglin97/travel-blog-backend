package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.ApiSuccess;
import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/blogs")
public interface BlogApi {

    @PostMapping
    ResponseEntity<ApiSuccess<BlogDto>> createBlog(@RequestBody @Valid BlogCreateRequestDto request);

    @GetMapping("/{id}")
    ResponseEntity<ApiSuccess<BlogDto>> getBlogById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<ApiSuccess<List<BlogDto>>> getAllBlogs();

    @PutMapping("/{id}")
    ResponseEntity<ApiSuccess<BlogDto>> updateBlog(@PathVariable Long id, @RequestBody BlogUpdateRequestDto request);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiSuccess<Void>> deleteBlog(@PathVariable Long id);

    @GetMapping("/author/{authorId}")
    ResponseEntity<ApiSuccess<List<BlogDto>>> getBlogsByAuthor(@PathVariable Long authorId);

    @GetMapping("/approved")
    ResponseEntity<ApiSuccess<List<BlogDto>>> getApprovedBlogs();
}
