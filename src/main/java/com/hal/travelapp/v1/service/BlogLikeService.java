package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.blog.BlogLikeResponseDto;

public interface BlogLikeService {
    BlogLikeResponseDto likeBlog(Long blogId, Long userId);
    BlogLikeResponseDto unlikeBlog(Long blogId, Long userId);
    BlogLikeResponseDto getLikeStatus(Long blogId, Long userId);
    long getLikeCount(Long blogId);
}

