package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogFavoriteResponseDto;
import com.hal.travelapp.v1.entity.domain.FavoriteBlog;
import com.hal.travelapp.v1.entity.domain.TravelBlog;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.FavoriteBlogRepo;
import com.hal.travelapp.v1.repository.TravelBlogRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.impl.FavoriteBlogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteBlogServiceTest {

    @Mock
    private FavoriteBlogRepo favoriteBlogRepo;

    @Mock
    private TravelBlogRepo travelBlogRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private BlogService blogService;

    @InjectMocks
    private FavoriteBlogServiceImpl favoriteBlogService;

    private User user;
    private TravelBlog blog;
    private FavoriteBlog favoriteBlog;

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

        favoriteBlog = new FavoriteBlog();
        favoriteBlog.setId(1L);
        favoriteBlog.setUser(user);
        favoriteBlog.setBlog(blog);
        favoriteBlog.setDeleted(false);
    }

    @Test
    void shouldAddToFavorites() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteBlogRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(false);
        when(favoriteBlogRepo.save(any(FavoriteBlog.class))).thenAnswer(invocation -> {
            FavoriteBlog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // When
        BlogFavoriteResponseDto result = favoriteBlogService.addToFavorites(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.blogId()).isEqualTo(1L);
        assertThat(result.favorited()).isTrue();
        assertThat(result.message()).contains("added to favorites");

        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(userRepo).findById(1L);
        verify(favoriteBlogRepo).existsByUserIdAndBlogId(1L, 1L);
        verify(favoriteBlogRepo).save(any(FavoriteBlog.class));
    }

    @Test
    void shouldNotDuplicateFavoriteWhenAlreadyFavorited() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteBlogRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(true);

        // When
        BlogFavoriteResponseDto result = favoriteBlogService.addToFavorites(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.favorited()).isTrue();
        assertThat(result.message()).contains("already in favorites");

        verify(favoriteBlogRepo, never()).save(any(FavoriteBlog.class));
    }

    @Test
    void shouldThrowExceptionWhenBlogNotFound() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> favoriteBlogService.addToFavorites(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Blog not found");

        verify(travelBlogRepo).findByIdAndDeletedFalse(999L);
        verify(favoriteBlogRepo, never()).save(any(FavoriteBlog.class));
    }

    @Test
    void shouldRemoveFromFavorites() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(favoriteBlogRepo.findByUserIdAndBlogId(1L, 1L)).thenReturn(Optional.of(favoriteBlog));
        when(favoriteBlogRepo.save(any(FavoriteBlog.class))).thenReturn(favoriteBlog);

        // When
        BlogFavoriteResponseDto result = favoriteBlogService.removeFromFavorites(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.blogId()).isEqualTo(1L);
        assertThat(result.favorited()).isFalse();
        assertThat(result.message()).contains("removed from favorites");

        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(favoriteBlogRepo).findByUserIdAndBlogId(1L, 1L);
        verify(favoriteBlogRepo).save(favoriteBlog);
    }

    @Test
    void shouldHandleRemoveWhenFavoriteDoesNotExist() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(favoriteBlogRepo.findByUserIdAndBlogId(1L, 1L)).thenReturn(Optional.empty());

        // When
        BlogFavoriteResponseDto result = favoriteBlogService.removeFromFavorites(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.favorited()).isFalse();

        verify(favoriteBlogRepo, never()).save(any(FavoriteBlog.class));
    }

    @Test
    void shouldCheckIfFavorited() {
        // Given
        when(favoriteBlogRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(true);

        // When
        boolean result = favoriteBlogService.isFavorited(1L, 1L);

        // Then
        assertThat(result).isTrue();
        verify(favoriteBlogRepo).existsByUserIdAndBlogId(1L, 1L);
    }

    @Test
    void shouldGetFavoriteBlogs() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TravelBlog> blogPage = new PageImpl<>(List.of(blog), pageable, 1);
        BlogDto blogDto = new BlogDto(
                1L, "Test Blog", null, null, null, null, null, null, null, null,
                1L, "City", 1L, "Author", "APPROVED", null, null, null, null,
                0L, false, true, null, null
        );

        when(favoriteBlogRepo.findFavoriteBlogsByUserId(1L, TravelBlog.BlogStatus.APPROVED, pageable))
                .thenReturn(blogPage);
        when(blogService.mapToDto(blog)).thenReturn(blogDto);

        // When
        PageResult<BlogDto> result = favoriteBlogService.getFavoriteBlogs(1L, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().id()).isEqualTo(1L);

        verify(favoriteBlogRepo).findFavoriteBlogsByUserId(1L, TravelBlog.BlogStatus.APPROVED, pageable);
        verify(blogService).mapToDto(blog);
    }
}

