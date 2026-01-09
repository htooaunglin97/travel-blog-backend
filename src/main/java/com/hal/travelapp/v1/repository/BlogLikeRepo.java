package com.hal.travelapp.v1.repository;

import com.hal.travelapp.v1.entity.domain.BlogLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogLikeRepo extends JpaRepository<BlogLike, Long> {
    
    Optional<BlogLike> findByUserIdAndBlogId(Long userId, Long blogId);
    
    boolean existsByUserIdAndBlogId(Long userId, Long blogId);
    
    long countByBlogId(Long blogId);
    
    @Query("SELECT COUNT(bl) FROM BlogLike bl WHERE bl.blog.id = :blogId AND bl.deleted = false")
    long countLikesByBlogId(@Param("blogId") Long blogId);
}

