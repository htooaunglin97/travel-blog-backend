package com.hal.travelapp.v1.repository;

import com.hal.travelapp.v1.entity.domain.FavoriteBlog;
import com.hal.travelapp.v1.entity.domain.TravelBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteBlogRepo extends JpaRepository<FavoriteBlog, Long> {
    
    Optional<FavoriteBlog> findByUserIdAndBlogId(Long userId, Long blogId);
    
    boolean existsByUserIdAndBlogId(Long userId, Long blogId);
    
    @Query("SELECT fb.blog FROM FavoriteBlog fb WHERE fb.user.id = :userId AND fb.deleted = false AND fb.blog.deleted = false AND fb.blog.status = :status")
    Page<TravelBlog> findFavoriteBlogsByUserId(@Param("userId") Long userId, @Param("status") TravelBlog.BlogStatus status, Pageable pageable);
    
    @Query("SELECT fb FROM FavoriteBlog fb WHERE fb.user.id = :userId AND fb.deleted = false")
    List<FavoriteBlog> findAllByUserId(@Param("userId") Long userId);
}

