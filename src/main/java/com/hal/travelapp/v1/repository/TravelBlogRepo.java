package com.hal.travelapp.v1.repository;

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
public interface TravelBlogRepo extends JpaRepository<TravelBlog, Long> {
    
    List<TravelBlog> findByDeletedFalse();
    
    Optional<TravelBlog> findByIdAndDeletedFalse(Long id);
    
    List<TravelBlog> findByStatusAndDeletedFalse(TravelBlog.BlogStatus status);
    
    List<TravelBlog> findByAuthorIdAndDeletedFalse(Long authorId);
    
    @Query("SELECT b FROM TravelBlog b WHERE b.deleted = false AND b.status = :status")
    Page<TravelBlog> findApprovedBlogs(@Param("status") TravelBlog.BlogStatus status, Pageable pageable);
}


