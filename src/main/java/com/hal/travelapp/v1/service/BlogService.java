package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;

import java.util.List;

public interface BlogService {

    BlogDto createBlog(BlogCreateRequestDto createRequest, Long authorId);

    BlogDto updateBlog(Long id, BlogUpdateRequestDto updateRequest);

    void deleteBlog(Long id);

    List<BlogDto> getAllBlogs();

    BlogDto getBlogById(Long id);

    List<BlogDto> getBlogsByAuthor(Long authorId);

    List<BlogDto> getApprovedBlogs();
}
