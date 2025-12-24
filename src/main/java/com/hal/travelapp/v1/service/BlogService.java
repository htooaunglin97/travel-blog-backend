package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.domain.TravelBlog;

import java.util.List;

public interface BlogService {
    BlogDto createBlog(BlogCreateRequestDto createRequest, Long authorId);
    
    BlogDto getBlogById(Long id);
    
    List<BlogDto> getAllBlogs();
    
    BlogDto updateBlog(Long id, BlogUpdateRequestDto updateRequest);
    
    void deleteBlog(Long id);
    
    List<BlogDto> getBlogsByAuthor(Long authorId);
    
    List<BlogDto> getApprovedBlogs();
    
    BlogDto mapToDto(TravelBlog blog);
}

