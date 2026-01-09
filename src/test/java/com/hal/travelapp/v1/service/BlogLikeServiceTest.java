package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.blog.BlogLikeResponseDto;
import com.hal.travelapp.v1.entity.domain.BlogLike;
import com.hal.travelapp.v1.entity.domain.TravelBlog;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.BlogLikeRepo;
import com.hal.travelapp.v1.repository.TravelBlogRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.impl.BlogLikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogLikeServiceTest {

    @Mock
    private BlogLikeRepo blogLikeRepo;

    @Mock
    private TravelBlogRepo travelBlogRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private BlogLikeServiceImpl blogLikeService;

    private User user;
    private TravelBlog blog;
    private BlogLike blogLike;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        blog = new TravelBlog();
        blog.setId(1L);
        blog.setTitle("Test Blog");
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        blog.setDeleted(false);

        blogLike = new BlogLike();
        blogLike.setId(1L);
        blogLike.setUser(user);
        blogLike.setBlog(blog);
        blogLike.setDeleted(false);
    }

    @Test
    void shouldLikeBlog() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(blogLikeRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(false);
        when(blogLikeRepo.save(any(BlogLike.class))).thenAnswer(invocation -> {
            BlogLike saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(1L);

        // When
        BlogLikeResponseDto result = blogLikeService.likeBlog(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.blogId()).isEqualTo(1L);
        assertThat(result.liked()).isTrue();
        assertThat(result.likeCount()).isEqualTo(1L);

        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(userRepo).findById(1L);
        verify(blogLikeRepo).existsByUserIdAndBlogId(1L, 1L);
        verify(blogLikeRepo).save(any(BlogLike.class));
        verify(blogLikeRepo).countLikesByBlogId(1L);
    }

    @Test
    void shouldNotDuplicateLikeWhenAlreadyLiked() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(blogLikeRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(true);
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(1L);

        // When
        BlogLikeResponseDto result = blogLikeService.likeBlog(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.liked()).isTrue();
        assertThat(result.likeCount()).isEqualTo(1L);

        verify(blogLikeRepo, never()).save(any(BlogLike.class));
    }

    @Test
    void shouldThrowExceptionWhenBlogNotFound() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> blogLikeService.likeBlog(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Blog not found");

        verify(travelBlogRepo).findByIdAndDeletedFalse(999L);
        verify(blogLikeRepo, never()).save(any(BlogLike.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> blogLikeService.likeBlog(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(userRepo).findById(999L);
        verify(blogLikeRepo, never()).save(any(BlogLike.class));
    }

    @Test
    void shouldUnlikeBlog() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(blogLikeRepo.findByUserIdAndBlogId(1L, 1L)).thenReturn(Optional.of(blogLike));
        when(blogLikeRepo.save(any(BlogLike.class))).thenReturn(blogLike);
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

        // When
        BlogLikeResponseDto result = blogLikeService.unlikeBlog(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.blogId()).isEqualTo(1L);
        assertThat(result.liked()).isFalse();
        assertThat(result.likeCount()).isEqualTo(0L);

        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(blogLikeRepo).findByUserIdAndBlogId(1L, 1L);
        verify(blogLikeRepo).save(blogLike);
        verify(blogLikeRepo).countLikesByBlogId(1L);
    }

    @Test
    void shouldHandleUnlikeWhenLikeDoesNotExist() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(blogLikeRepo.findByUserIdAndBlogId(1L, 1L)).thenReturn(Optional.empty());
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

        // When
        BlogLikeResponseDto result = blogLikeService.unlikeBlog(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.liked()).isFalse();
        assertThat(result.likeCount()).isEqualTo(0L);

        verify(blogLikeRepo, never()).save(any(BlogLike.class));
    }

    @Test
    void shouldGetLikeStatus() {
        // Given
        when(blogLikeRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(true);
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(5L);

        // When
        BlogLikeResponseDto result = blogLikeService.getLikeStatus(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.blogId()).isEqualTo(1L);
        assertThat(result.liked()).isTrue();
        assertThat(result.likeCount()).isEqualTo(5L);

        verify(blogLikeRepo).existsByUserIdAndBlogId(1L, 1L);
        verify(blogLikeRepo).countLikesByBlogId(1L);
    }

    @Test
    void shouldGetLikeCount() {
        // Given
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(10L);

        // When
        long result = blogLikeService.getLikeCount(1L);

        // Then
        assertThat(result).isEqualTo(10L);
        verify(blogLikeRepo).countLikesByBlogId(1L);
    }
}

