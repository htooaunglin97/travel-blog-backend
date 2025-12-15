package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.domain.*;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.*;
import com.hal.travelapp.v1.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final TravelBlogRepo travelBlogRepo;
    private final CityRepo cityRepo;
    private final TravelCategoryRepo travelCategoryRepo;
    private final UserRepo userRepo;


    public BlogDto createBlog(BlogCreateRequestDto createRequest, Long authorId) {
        // Validate city exists
        City city = cityRepo.findById(createRequest.cityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + createRequest.cityId()));

        // Validate author exists
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

        // Get categories
        Set<TravelCategory> categories = Set.of();
        if (createRequest.categoryIds() != null && !createRequest.categoryIds().isEmpty()) {
            categories = new HashSet<>(travelCategoryRepo.findByIdIn(createRequest.categoryIds()));
        }

        // Create blog entity
        TravelBlog blog = new TravelBlog();
        blog.setTitle(createRequest.title());
        blog.setMainPhotoUrl(createRequest.mainPhotoUrl());
        blog.setParagraph1(createRequest.paragraph1());
        blog.setParagraph2(createRequest.paragraph2());
        blog.setParagraph3(createRequest.paragraph3());
        blog.setMidPhoto1Url(createRequest.midPhoto1Url());
        blog.setMidPhoto2Url(createRequest.midPhoto2Url());
        blog.setMidPhoto3Url(createRequest.midPhoto3Url());
        blog.setSidePhotoUrl(createRequest.sidePhotoUrl());
        blog.setCity(city);
        blog.setAuthor(author);
        blog.setStatus(TravelBlog.BlogStatus.PENDING);
        blog.setTravelCategory(categories);

        // Create BestTimeToVisit if provided
        if (createRequest.bestTimeStartMonth() != null && createRequest.bestTimeEndMonth() != null) {
            BestTimeToVisit bestTime = new BestTimeToVisit();
            bestTime.setStartMonth(createRequest.bestTimeStartMonth().intValue());
            bestTime.setEndMonth(createRequest.bestTimeEndMonth().intValue());
            bestTime.setTravelBlog(blog);
            blog.setBestTimeToVisit(bestTime);
        }

        TravelBlog savedBlog = travelBlogRepo.save(blog);

        return mapToDto(savedBlog);
    }

    public BlogDto getBlogById(Long id) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
        return mapToDto(blog);
    }

    public List<BlogDto> getAllBlogs() {
        return travelBlogRepo.findByDeletedFalse()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public BlogDto updateBlog(Long id, BlogUpdateRequestDto updateRequest) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));

        // Update fields if provided
        if (updateRequest.title() != null) {
            blog.setTitle(updateRequest.title());
        }
        if (updateRequest.mainPhotoUrl() != null) {
            blog.setMainPhotoUrl(updateRequest.mainPhotoUrl());
        }
        if (updateRequest.paragraph1() != null) {
            blog.setParagraph1(updateRequest.paragraph1());
        }
        if (updateRequest.paragraph2() != null) {
            blog.setParagraph2(updateRequest.paragraph2());
        }
        if (updateRequest.paragraph3() != null) {
            blog.setParagraph3(updateRequest.paragraph3());
        }
        if (updateRequest.midPhoto1Url() != null) {
            blog.setMidPhoto1Url(updateRequest.midPhoto1Url());
        }
        if (updateRequest.midPhoto2Url() != null) {
            blog.setMidPhoto2Url(updateRequest.midPhoto2Url());
        }
        if (updateRequest.midPhoto3Url() != null) {
            blog.setMidPhoto3Url(updateRequest.midPhoto3Url());
        }
        if (updateRequest.sidePhotoUrl() != null) {
            blog.setSidePhotoUrl(updateRequest.sidePhotoUrl());
        }
        if (updateRequest.cityId() != null) {
            City city = cityRepo.findById(updateRequest.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + updateRequest.cityId()));
            blog.setCity(city);
        }
        if (updateRequest.categoryIds() != null && !updateRequest.categoryIds().isEmpty()) {
            Set<TravelCategory> categories = new HashSet<>(travelCategoryRepo.findByIdIn(updateRequest.categoryIds()));
            blog.setTravelCategory(categories);
        }
        if (updateRequest.bestTimeStartMonth() != null && updateRequest.bestTimeEndMonth() != null) {
            BestTimeToVisit bestTime = blog.getBestTimeToVisit();
            if (bestTime == null) {
                bestTime = new BestTimeToVisit();
                bestTime.setTravelBlog(blog);
            }
            bestTime.setStartMonth(updateRequest.bestTimeStartMonth().intValue());
            bestTime.setEndMonth(updateRequest.bestTimeEndMonth().intValue());
            blog.setBestTimeToVisit(bestTime);
        }

        TravelBlog updatedBlog = travelBlogRepo.save(blog);
        return mapToDto(updatedBlog);
    }

    public void deleteBlog(Long id) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
        blog.setDeleted(true);
        travelBlogRepo.save(blog);
    }

    public List<BlogDto> getBlogsByAuthor(Long authorId) {
        return travelBlogRepo.findByAuthorIdAndDeletedFalse(authorId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<BlogDto> getApprovedBlogs() {
        return travelBlogRepo.findApprovedBlogs(TravelBlog.BlogStatus.APPROVED)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private BlogDto mapToDto(TravelBlog blog) {
        return new BlogDto(
                blog.getId(),
                blog.getTitle(),
                blog.getMainPhotoUrl(),
                blog.getParagraph1(),
                blog.getParagraph2(),
                blog.getParagraph3(),
                blog.getMidPhoto1Url(),
                blog.getMidPhoto2Url(),
                blog.getMidPhoto3Url(),
                blog.getSidePhotoUrl(),
                blog.getCity() != null ? blog.getCity().getId() : null,
                blog.getCity() != null ? blog.getCity().getName() : null,
                blog.getAuthor() != null ? blog.getAuthor().getId() : null,
                blog.getAuthor() != null ? blog.getAuthor().getName() : null,
                blog.getStatus() != null ? blog.getStatus().name() : null,
                blog.getBestTimeToVisit() != null ? (long) blog.getBestTimeToVisit().getStartMonth() : null,
                blog.getBestTimeToVisit() != null ? (long) blog.getBestTimeToVisit().getEndMonth() : null,
                blog.getTravelCategory() != null ? blog.getTravelCategory().stream()
                        .map(TravelCategory::getId)
                        .collect(Collectors.toSet()) : Set.of(),
                blog.getTravelCategory() != null ? blog.getTravelCategory().stream()
                        .map(TravelCategory::getName)
                        .collect(Collectors.toSet()) : Set.of(),
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }
}

