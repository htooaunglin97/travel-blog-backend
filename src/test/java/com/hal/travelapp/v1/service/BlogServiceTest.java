package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.BlogDto;
import com.hal.travelapp.v1.dto.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.domain.*;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.*;
import com.hal.travelapp.v1.service.impl.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @Mock
    private TravelBlogRepo travelBlogRepo;

    @Mock
    private CityRepo cityRepo;

    @Mock
    private TravelCategoryRepo travelCategoryRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private BlogService blogService;

    private User author;
    private City city;
    private TravelCategory category;
    private TravelBlog blog;

    @BeforeEach
    void setUp() {
        Role role = new Role(RoleEnum.ROLE_CERTIFIED_USER);
        role.setId(1L);

        author = new User();
        author.setId(1L);
        author.setName("John Doe");
        author.setEmail("john@example.com");
        author.setRole(role);

        city = new City();
        city.setId(1L);
        city.setName("Yangon");

        category = new TravelCategory();
        category.setId(1L);
        category.setName("Honeymoon");

        blog = new TravelBlog();
        blog.setId(1L);
        blog.setTitle("Amazing Yangon");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("First paragraph");
        blog.setParagraph2("Second paragraph");
        blog.setParagraph3("Third paragraph");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(city);
        blog.setAuthor(author);
        blog.setStatus(TravelBlog.BlogStatus.PENDING);
        blog.setTravelCategory(Set.of(category));
        blog.setCreatedAt(Instant.now());
        blog.setUpdatedAt(Instant.now());
        blog.setDeleted(false);
    }

    @Test
    void shouldCreateBlog() {
        // Given
        BlogCreateRequestDto createRequest = new BlogCreateRequestDto(
                "Amazing Yangon",
                "main.jpg",
                "First paragraph",
                "Second paragraph",
                "Third paragraph",
                "mid1.jpg",
                "mid2.jpg",
                "mid3.jpg",
                "side.jpg",
                1L,
                1L,
                3L,
                Set.of(1L)
        );

        when(cityRepo.findById(1L)).thenReturn(Optional.of(city));
        when(travelCategoryRepo.findByIdIn(Set.of(1L))).thenReturn(List.of(category));
        when(userRepo.findById(1L)).thenReturn(Optional.of(author));
        when(travelBlogRepo.save(any(TravelBlog.class))).thenAnswer(invocation -> {
            TravelBlog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // When
        BlogDto result = blogService.createBlog(createRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Amazing Yangon");
        assertThat(result.mainPhotoUrl()).isEqualTo("main.jpg");
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.cityId()).isEqualTo(1L);
        assertThat(result.authorId()).isEqualTo(1L);

        verify(cityRepo).findById(1L);
        verify(travelCategoryRepo).findByIdIn(Set.of(1L));
        verify(userRepo).findById(1L);
        verify(travelBlogRepo).save(any(TravelBlog.class));
    }

    @Test
    void shouldThrowExceptionWhenCityNotFound() {
        // Given
        BlogCreateRequestDto createRequest = new BlogCreateRequestDto(
                "Amazing Yangon",
                "main.jpg",
                "First paragraph",
                "Second paragraph",
                "Third paragraph",
                "mid1.jpg",
                "mid2.jpg",
                "mid3.jpg",
                "side.jpg",
                999L,
                1L,
                3L,
                Set.of(1L)
        );

        when(cityRepo.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> blogService.createBlog(createRequest, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("City not found");

        verify(cityRepo).findById(999L);
        verify(travelBlogRepo, never()).save(any(TravelBlog.class));
    }

    @Test
    void shouldGetBlogById() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));

        // When
        BlogDto result = blogService.getBlogById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Amazing Yangon");
        assertThat(result.cityId()).isEqualTo(1L);
        assertThat(result.authorId()).isEqualTo(1L);

        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
    }

    @Test
    void shouldThrowExceptionWhenBlogNotFound() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> blogService.getBlogById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Blog not found");

        verify(travelBlogRepo).findByIdAndDeletedFalse(999L);
    }

    @Test
    void shouldGetAllBlogs() {
        // Given
        when(travelBlogRepo.findByDeletedFalse()).thenReturn(List.of(blog));

        // When
        List<BlogDto> result = blogService.getAllBlogs();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("Amazing Yangon");

        verify(travelBlogRepo).findByDeletedFalse();
    }

    @Test
    void shouldUpdateBlog() {
        // Given
        BlogUpdateRequestDto updateRequest = new BlogUpdateRequestDto(
                "Updated Title",
                "new-main.jpg",
                "Updated paragraph 1",
                "Updated paragraph 2",
                "Updated paragraph 3",
                "new-mid1.jpg",
                "new-mid2.jpg",
                "new-mid3.jpg",
                "new-side.jpg",
                1L,
                4L,
                6L,
                Set.of(1L)
        );

        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(cityRepo.findById(1L)).thenReturn(Optional.of(city));
        when(travelCategoryRepo.findByIdIn(Set.of(1L))).thenReturn(List.of(category));
        when(travelBlogRepo.save(any(TravelBlog.class))).thenReturn(blog);

        // When
        BlogDto result = blogService.updateBlog(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(travelBlogRepo).save(any(TravelBlog.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBlog() {
        // Given
        BlogUpdateRequestDto updateRequest = new BlogUpdateRequestDto(
                "Updated Title",
                null, null, null, null, null, null, null, null, null, null, null
        );

        when(travelBlogRepo.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> blogService.updateBlog(999L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Blog not found");

        verify(travelBlogRepo).findByIdAndDeletedFalse(999L);
        verify(travelBlogRepo, never()).save(any(TravelBlog.class));
    }

    @Test
    void shouldDeleteBlog() {
        // Given
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(travelBlogRepo.save(any(TravelBlog.class))).thenReturn(blog);

        // When
        blogService.deleteBlog(1L);

        // Then
        assertThat(blog.isDeleted()).isTrue();
        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(travelBlogRepo).save(blog);
    }

    @Test
    void shouldGetBlogsByAuthor() {
        // Given
        when(travelBlogRepo.findByAuthorIdAndDeletedFalse(1L)).thenReturn(List.of(blog));

        // When
        List<BlogDto> result = blogService.getBlogsByAuthor(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).authorId()).isEqualTo(1L);

        verify(travelBlogRepo).findByAuthorIdAndDeletedFalse(1L);
    }

    @Test
    void shouldGetApprovedBlogs() {
        // Given
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        when(travelBlogRepo.findApprovedBlogs(TravelBlog.BlogStatus.APPROVED)).thenReturn(List.of(blog));

        // When
        List<BlogDto> result = blogService.getApprovedBlogs();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo("APPROVED");

        verify(travelBlogRepo).findApprovedBlogs(TravelBlog.BlogStatus.APPROVED);
    }
}

