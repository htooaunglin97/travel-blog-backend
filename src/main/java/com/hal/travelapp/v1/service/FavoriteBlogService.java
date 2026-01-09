package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogFavoriteResponseDto;
import org.springframework.data.domain.Pageable;

public interface FavoriteBlogService {
    BlogFavoriteResponseDto addToFavorites(Long blogId, Long userId);
    BlogFavoriteResponseDto removeFromFavorites(Long blogId, Long userId);
    boolean isFavorited(Long blogId, Long userId);
    PageResult<BlogDto> getFavoriteBlogs(Long userId, Pageable pageable);
}

