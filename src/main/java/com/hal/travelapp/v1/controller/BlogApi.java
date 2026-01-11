package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.ApiSuccess;
import com.hal.travelapp.v1.dto.CursorPageResult;
import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogFavoriteResponseDto;
import com.hal.travelapp.v1.dto.blog.BlogLikeResponseDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/blogs")
public interface BlogApi {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiSuccess<BlogDto>> createBlog(@ModelAttribute @Valid BlogCreateRequestDto request);

    @GetMapping("/{id}")
    ResponseEntity<ApiSuccess<BlogDto>> getBlogById(@PathVariable Long id);

    @GetMapping
    ResponseEntity<ApiSuccess<PageResult<BlogDto>>> getAllBlogs(
           Pageable pageable
    );

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiSuccess<BlogDto>> updateBlog(@PathVariable Long id, @ModelAttribute @Valid BlogUpdateRequestDto request);

    @DeleteMapping("/{id}")
    ResponseEntity<ApiSuccess<Void>> deleteBlog(@PathVariable Long id);

    @GetMapping("/author/{authorId}")
    ResponseEntity<ApiSuccess<List<BlogDto>>> getBlogsByAuthor(@PathVariable Long authorId);

    @GetMapping("/approved")
    ResponseEntity<ApiSuccess<PageResult<BlogDto>>> getApprovedBlogs(
            Pageable pageable
    );

    @PostMapping("/{id}/like")
    ResponseEntity<ApiSuccess<BlogLikeResponseDto>> likeBlog(@PathVariable Long id);

    @DeleteMapping("/{id}/like")
    ResponseEntity<ApiSuccess<BlogLikeResponseDto>> unlikeBlog(@PathVariable Long id);

    @PostMapping("/{id}/favorite")
    ResponseEntity<ApiSuccess<BlogFavoriteResponseDto>> addToFavorites(@PathVariable Long id);

    @DeleteMapping("/{id}/favorite")
    ResponseEntity<ApiSuccess<BlogFavoriteResponseDto>> removeFromFavorites(@PathVariable Long id);

    @GetMapping("/favorites")
    ResponseEntity<ApiSuccess<PageResult<BlogDto>>> getFavoriteBlogs(Pageable pageable);

    @GetMapping("/featured")
    ResponseEntity<ApiSuccess<CursorPageResult<BlogDto>>> getFeaturedBlogs(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int pageSize
    );
}
