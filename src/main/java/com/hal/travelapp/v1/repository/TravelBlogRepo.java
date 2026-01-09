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
    
    @Query(value = """
        SELECT b.* FROM travel_blog_tbl b
        LEFT JOIN blog_like_tbl bl ON bl.blog_id = b.id AND bl.deleted = false
        WHERE b.deleted = false 
        AND b.status = :status
        AND (:cursorId IS NULL OR b.id < :cursorId)
        GROUP BY b.id
        ORDER BY COUNT(bl.id) DESC, b.id DESC
    """, nativeQuery = true)
    List<TravelBlog> findFeaturedBlogs(
        @Param("status") TravelBlog.BlogStatus status,
        @Param("cursorId") Long cursorId,
        org.springframework.data.domain.Pageable pageable
    );
}




