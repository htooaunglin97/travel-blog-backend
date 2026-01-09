package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.CursorPageResult;
import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.domain.TravelBlog;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    BlogDto createBlog(BlogCreateRequestDto createRequest, Long authorId);
    
    BlogDto getBlogById(Long id);
    
    BlogDto getBlogById(Long id, Long userId);
    
    PageResult<BlogDto> getAllBlogs(Pageable pageable);
    
    BlogDto updateBlog(Long id, BlogUpdateRequestDto updateRequest);
    
    void deleteBlog(Long id);
    
    List<BlogDto> getBlogsByAuthor(Long authorId);
    
    PageResult<BlogDto> getApprovedBlogs(Pageable pageable);
    
    CursorPageResult<BlogDto> getFeaturedBlogs(String cursor, int pageSize, Long userId);
    
    BlogDto mapToDto(TravelBlog blog);
    
    BlogDto mapToDto(TravelBlog blog, Long userId);
}



