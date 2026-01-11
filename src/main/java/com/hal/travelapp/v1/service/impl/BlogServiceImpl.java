package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.dto.CursorPageResult;
import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.domain.*;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.*;
import com.hal.travelapp.v1.service.BlogService;
import com.hal.travelapp.v1.service.ImageUploadService;
import com.hal.travelapp.v1.service.mapper.BlogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final BlogLikeRepo blogLikeRepo;
    private final FavoriteBlogRepo favoriteBlogRepo;
    private final ImageUploadService imageUploadService;


    @Override
    public BlogDto createBlog(BlogCreateRequestDto createRequest, Long authorId) {
        // Validate required photos
        if (createRequest.getMainPhoto() == null || createRequest.getMainPhoto().isEmpty()) {
            throw new IllegalArgumentException("Main photo is required");
        }
        if (createRequest.getMidPhoto1() == null || createRequest.getMidPhoto1().isEmpty()) {
            throw new IllegalArgumentException("First mid photo is required");
        }
        if (createRequest.getMidPhoto2() == null || createRequest.getMidPhoto2().isEmpty()) {
            throw new IllegalArgumentException("Second mid photo is required");
        }
        if (createRequest.getMidPhoto3() == null || createRequest.getMidPhoto3().isEmpty()) {
            throw new IllegalArgumentException("Third mid photo is required");
        }
        if (createRequest.getSidePhoto() == null || createRequest.getSidePhoto().isEmpty()) {
            throw new IllegalArgumentException("Side photo is required");
        }
        
        // Validate city exists
        City city = cityRepo.findById(createRequest.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + createRequest.getCityId()));

        // Validate author exists
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

        // Get categories
        Set<TravelCategory> categories = Set.of();
        if (createRequest.getCategoryIds() != null && !createRequest.getCategoryIds().isEmpty()) {
            categories = new HashSet<>(travelCategoryRepo.findByIdIn(createRequest.getCategoryIds()));
        }

        // Upload images and get URLs
        String mainPhotoUrl = uploadImageIfPresent(createRequest.getMainPhoto(), "main");
        String midPhoto1Url = uploadImageIfPresent(createRequest.getMidPhoto1(), "mid1");
        String midPhoto2Url = uploadImageIfPresent(createRequest.getMidPhoto2(), "mid2");
        String midPhoto3Url = uploadImageIfPresent(createRequest.getMidPhoto3(), "mid3");
        String sidePhotoUrl = uploadImageIfPresent(createRequest.getSidePhoto(), "side");

        // Create blog entity
        TravelBlog blog = new TravelBlog();
        blog.setTitle(createRequest.getTitle());
        blog.setMainPhotoUrl(mainPhotoUrl);
        blog.setParagraph1(createRequest.getParagraph1());
        blog.setParagraph2(createRequest.getParagraph2());
        blog.setParagraph3(createRequest.getParagraph3());
        blog.setMidPhoto1Url(midPhoto1Url);
        blog.setMidPhoto2Url(midPhoto2Url);
        blog.setMidPhoto3Url(midPhoto3Url);
        blog.setSidePhotoUrl(sidePhotoUrl);
        blog.setCity(city);
        blog.setAuthor(author);
        blog.setStatus(TravelBlog.BlogStatus.PENDING);
        blog.setTravelCategory(categories);

        // Create BestTimeToVisit if provided
        if (createRequest.getBestTimeStartMonth() != null && createRequest.getBestTimeEndMonth() != null) {
            BestTimeToVisit bestTime = new BestTimeToVisit();
            bestTime.setStartMonth(createRequest.getBestTimeStartMonth().intValue());
            bestTime.setEndMonth(createRequest.getBestTimeEndMonth().intValue());
            bestTime.setTravelBlog(blog);
            blog.setBestTimeToVisit(bestTime);
        }

        TravelBlog savedBlog = travelBlogRepo.save(blog);

        return mapToDto(savedBlog);
    }

    @Override
    public BlogDto getBlogById(Long id) {
        // Only return approved blogs for public viewing
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
        
        // Only show approved blogs to public
        if (blog.getStatus() != TravelBlog.BlogStatus.APPROVED) {
            throw new ResourceNotFoundException("Blog not found with id: " + id);
        }
        
        return mapToDto(blog);
    }

    @Override
    public BlogDto getBlogById(Long id, Long userId) {
        // Only return approved blogs for public viewing
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
        
        // Only show approved blogs to public
        if (blog.getStatus() != TravelBlog.BlogStatus.APPROVED) {
            throw new ResourceNotFoundException("Blog not found with id: " + id);
        }
        
        return mapToDto(blog, userId);
    }

    @Override
    public PageResult<BlogDto> getAllBlogs(Pageable pageable) {
        // Only return approved blogs for public viewing
        Page<TravelBlog> blogPage =  travelBlogRepo.findApprovedBlogs(TravelBlog.BlogStatus.APPROVED, pageable);

        Page<BlogDto> blogDtoPage = blogPage.map(this::mapToDto);


        return PageResult.of(blogDtoPage);
    }

    @Override
    public BlogDto updateBlog(Long id, BlogUpdateRequestDto updateRequest) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));

        // Update fields if provided
        if (updateRequest.getTitle() != null) {
            blog.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getMainPhoto() != null && !updateRequest.getMainPhoto().isEmpty()) {
            // Delete old image if exists
            if (blog.getMainPhotoUrl() != null) {
                try {
                    imageUploadService.deleteImage(blog.getMainPhotoUrl());
                } catch (Exception e) {
                    // Log error but continue with upload
                }
            }
            blog.setMainPhotoUrl(uploadImageIfPresent(updateRequest.getMainPhoto(), "main"));
        }
        if (updateRequest.getParagraph1() != null) {
            blog.setParagraph1(updateRequest.getParagraph1());
        }
        if (updateRequest.getParagraph2() != null) {
            blog.setParagraph2(updateRequest.getParagraph2());
        }
        if (updateRequest.getParagraph3() != null) {
            blog.setParagraph3(updateRequest.getParagraph3());
        }
        if (updateRequest.getMidPhoto1() != null && !updateRequest.getMidPhoto1().isEmpty()) {
            // Delete old image if exists
            if (blog.getMidPhoto1Url() != null) {
                try {
                    imageUploadService.deleteImage(blog.getMidPhoto1Url());
                } catch (Exception e) {
                    // Log error but continue with upload
                }
            }
            blog.setMidPhoto1Url(uploadImageIfPresent(updateRequest.getMidPhoto1(), "mid1"));
        }
        if (updateRequest.getMidPhoto2() != null && !updateRequest.getMidPhoto2().isEmpty()) {
            // Delete old image if exists
            if (blog.getMidPhoto2Url() != null) {
                try {
                    imageUploadService.deleteImage(blog.getMidPhoto2Url());
                } catch (Exception e) {
                    // Log error but continue with upload
                }
            }
            blog.setMidPhoto2Url(uploadImageIfPresent(updateRequest.getMidPhoto2(), "mid2"));
        }
        if (updateRequest.getMidPhoto3() != null && !updateRequest.getMidPhoto3().isEmpty()) {
            // Delete old image if exists
            if (blog.getMidPhoto3Url() != null) {
                try {
                    imageUploadService.deleteImage(blog.getMidPhoto3Url());
                } catch (Exception e) {
                    // Log error but continue with upload
                }
            }
            blog.setMidPhoto3Url(uploadImageIfPresent(updateRequest.getMidPhoto3(), "mid3"));
        }
        if (updateRequest.getSidePhoto() != null && !updateRequest.getSidePhoto().isEmpty()) {
            // Delete old image if exists
            if (blog.getSidePhotoUrl() != null) {
                try {
                    imageUploadService.deleteImage(blog.getSidePhotoUrl());
                } catch (Exception e) {
                    // Log error but continue with upload
                }
            }
            blog.setSidePhotoUrl(uploadImageIfPresent(updateRequest.getSidePhoto(), "side"));
        }
        if (updateRequest.getCityId() != null) {
            City city = cityRepo.findById(updateRequest.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found with id: " + updateRequest.getCityId()));
            blog.setCity(city);
        }
        if (updateRequest.getCategoryIds() != null && !updateRequest.getCategoryIds().isEmpty()) {
            Set<TravelCategory> categories = new HashSet<>(travelCategoryRepo.findByIdIn(updateRequest.getCategoryIds()));
            blog.setTravelCategory(categories);
        }
        if (updateRequest.getBestTimeStartMonth() != null && updateRequest.getBestTimeEndMonth() != null) {
            BestTimeToVisit bestTime = blog.getBestTimeToVisit();
            if (bestTime == null) {
                bestTime = new BestTimeToVisit();
                bestTime.setTravelBlog(blog);
            }
            bestTime.setStartMonth(updateRequest.getBestTimeStartMonth().intValue());
            bestTime.setEndMonth(updateRequest.getBestTimeEndMonth().intValue());
            blog.setBestTimeToVisit(bestTime);
        }

        TravelBlog updatedBlog = travelBlogRepo.save(blog);
        return mapToDto(updatedBlog);
    }

    @Override
    public void deleteBlog(Long id) {
        TravelBlog blog = travelBlogRepo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + id));
        
        // Delete images from GitHub
        deleteImageIfPresent(blog.getMainPhotoUrl());
        deleteImageIfPresent(blog.getMidPhoto1Url());
        deleteImageIfPresent(blog.getMidPhoto2Url());
        deleteImageIfPresent(blog.getMidPhoto3Url());
        deleteImageIfPresent(blog.getSidePhotoUrl());
        
        blog.setDeleted(true);
        travelBlogRepo.save(blog);
    }

    @Override
    public List<BlogDto> getBlogsByAuthor(Long authorId) {
        return travelBlogRepo.findByAuthorIdAndDeletedFalse(authorId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<BlogDto> getApprovedBlogs(Pageable pageable) {
        Page<TravelBlog> blogPage = travelBlogRepo.findApprovedBlogs(TravelBlog.BlogStatus.APPROVED, pageable);

        Page<BlogDto> blogDtoPage = blogPage.map(this::mapToDto);

        return PageResult.of(blogDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResult<BlogDto> getFeaturedBlogs(String cursor, int pageSize, Long userId) {
        Long cursorId = null;
        if (cursor != null && !cursor.isEmpty()) {
            try {
                cursorId = Long.parseLong(cursor);
            } catch (NumberFormatException e) {
                // Invalid cursor, treat as null
            }
        }

        Pageable pageable = PageRequest.of(0, pageSize + 1); // Fetch one extra to check if there's more
        List<TravelBlog> blogs = travelBlogRepo.findFeaturedBlogs(
                TravelBlog.BlogStatus.APPROVED,
                cursorId,
                pageable
        );

        boolean hasNext = blogs.size() > pageSize;
        List<TravelBlog> blogsToReturn = hasNext ? blogs.subList(0, pageSize) : blogs;

        List<BlogDto> blogDtos = blogsToReturn.stream()
                .map(blog -> userId != null ? mapToDto(blog, userId) : mapToDto(blog))
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext && !blogsToReturn.isEmpty()) {
            nextCursor = String.valueOf(blogsToReturn.get(blogsToReturn.size() - 1).getId());
        }

        return CursorPageResult.of(blogDtos, nextCursor, hasNext, pageSize);
    }

    @Override
    public BlogDto mapToDto(TravelBlog blog) {
        long likeCount = blogLikeRepo.countLikesByBlogId(blog.getId());
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
                likeCount,
                null, // isLiked - null for unauthenticated users
                null, // isFavorited - null for unauthenticated users
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }

    @Override
    public BlogDto mapToDto(TravelBlog blog, Long userId) {
        long likeCount = blogLikeRepo.countLikesByBlogId(blog.getId());
        boolean isLiked = blogLikeRepo.existsByUserIdAndBlogId(userId, blog.getId());
        boolean isFavorited = favoriteBlogRepo.existsByUserIdAndBlogId(userId, blog.getId());
        
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
                likeCount,
                isLiked,
                isFavorited,
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }
    
    /**
     * Helper method to upload image if present, returns null if file is null or empty
     */
    private String uploadImageIfPresent(MultipartFile file, String prefix) {
        if (file != null && !file.isEmpty()) {
            return imageUploadService.uploadImage(file, null);
        }
        return null;
    }
    
    /**
     * Helper method to delete image from GitHub if URL is present
     */
    private void deleteImageIfPresent(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                imageUploadService.deleteImage(imageUrl);
            } catch (Exception e) {
                // Log error but don't fail the deletion
                // Image might already be deleted or URL might be invalid
            }
        }
    }
}

