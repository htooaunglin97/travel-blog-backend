package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.dto.blog.BlogLikeResponseDto;
import com.hal.travelapp.v1.entity.domain.BlogLike;
import com.hal.travelapp.v1.entity.domain.TravelBlog;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.BlogLikeRepo;
import com.hal.travelapp.v1.repository.TravelBlogRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.BlogLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogLikeServiceImpl implements BlogLikeService {

    private final BlogLikeRepo blogLikeRepo;
    private final TravelBlogRepo travelBlogRepo;
    private final UserRepo userRepo;

    @Override
    public BlogLikeResponseDto likeBlog(Long blogId, Long userId) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + blogId));
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if already liked
        if (blogLikeRepo.existsByUserIdAndBlogId(userId, blogId)) {
            return getLikeStatus(blogId, userId);
        }

        BlogLike blogLike = new BlogLike();
        blogLike.setUser(user);
        blogLike.setBlog(blog);
        blogLikeRepo.save(blogLike);

        long likeCount = blogLikeRepo.countLikesByBlogId(blogId);
        return new BlogLikeResponseDto(blogId, true, likeCount);
    }

    @Override
    public BlogLikeResponseDto unlikeBlog(Long blogId, Long userId) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + blogId));

        BlogLike blogLike = blogLikeRepo.findByUserIdAndBlogId(userId, blogId)
                .orElse(null);

        if (blogLike != null && !blogLike.isDeleted()) {
            blogLike.setDeleted(true);
            blogLikeRepo.save(blogLike);
        }

        long likeCount = blogLikeRepo.countLikesByBlogId(blogId);
        return new BlogLikeResponseDto(blogId, false, likeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogLikeResponseDto getLikeStatus(Long blogId, Long userId) {
        boolean isLiked = blogLikeRepo.existsByUserIdAndBlogId(userId, blogId);
        long likeCount = blogLikeRepo.countLikesByBlogId(blogId);
        return new BlogLikeResponseDto(blogId, isLiked, likeCount);
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(Long blogId) {
        return blogLikeRepo.countLikesByBlogId(blogId);
    }
}

