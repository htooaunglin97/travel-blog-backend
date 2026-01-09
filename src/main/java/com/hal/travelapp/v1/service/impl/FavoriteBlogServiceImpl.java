package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogFavoriteResponseDto;
import com.hal.travelapp.v1.entity.domain.FavoriteBlog;
import com.hal.travelapp.v1.entity.domain.TravelBlog;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.FavoriteBlogRepo;
import com.hal.travelapp.v1.repository.TravelBlogRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.BlogService;
import com.hal.travelapp.v1.service.FavoriteBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FavoriteBlogServiceImpl implements FavoriteBlogService {

    private final FavoriteBlogRepo favoriteBlogRepo;
    private final TravelBlogRepo travelBlogRepo;
    private final UserRepo userRepo;
    private final BlogService blogService;

    @Override
    public BlogFavoriteResponseDto addToFavorites(Long blogId, Long userId) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + blogId));
        
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if already favorited
        if (favoriteBlogRepo.existsByUserIdAndBlogId(userId, blogId)) {
            return new BlogFavoriteResponseDto(blogId, true, "Blog is already in favorites");
        }

        FavoriteBlog favoriteBlog = new FavoriteBlog();
        favoriteBlog.setUser(user);
        favoriteBlog.setBlog(blog);
        favoriteBlogRepo.save(favoriteBlog);

        return new BlogFavoriteResponseDto(blogId, true, "Blog added to favorites successfully");
    }

    @Override
    public BlogFavoriteResponseDto removeFromFavorites(Long blogId, Long userId) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + blogId));

        FavoriteBlog favoriteBlog = favoriteBlogRepo.findByUserIdAndBlogId(userId, blogId)
                .orElse(null);

        if (favoriteBlog != null && !favoriteBlog.isDeleted()) {
            favoriteBlog.setDeleted(true);
            favoriteBlogRepo.save(favoriteBlog);
        }

        return new BlogFavoriteResponseDto(blogId, false, "Blog removed from favorites successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorited(Long blogId, Long userId) {
        return favoriteBlogRepo.existsByUserIdAndBlogId(userId, blogId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<BlogDto> getFavoriteBlogs(Long userId, Pageable pageable) {
        Page<TravelBlog> favoriteBlogsPage = favoriteBlogRepo.findFavoriteBlogsByUserId(
                userId, 
                TravelBlog.BlogStatus.APPROVED, 
                pageable
        );

        Page<BlogDto> blogDtoPage = favoriteBlogsPage.map(blogService::mapToDto);
        return PageResult.of(blogDtoPage);
    }
}

