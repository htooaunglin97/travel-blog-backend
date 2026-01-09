package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.CursorPageResult;
import com.hal.travelapp.v1.dto.PageResult;
import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.domain.*;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.*;
import com.hal.travelapp.v1.service.impl.BlogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

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

    @Mock
    private BlogLikeRepo blogLikeRepo;

    @Mock
    private FavoriteBlogRepo favoriteBlogRepo;

    @InjectMocks
    private BlogServiceImpl blogService;

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
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

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
        verify(blogLikeRepo).countLikesByBlogId(1L);
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
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        when(travelBlogRepo.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(blog));
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

        // When
        BlogDto result = blogService.getBlogById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Amazing Yangon");
        assertThat(result.cityId()).isEqualTo(1L);
        assertThat(result.authorId()).isEqualTo(1L);

        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(blogLikeRepo).countLikesByBlogId(1L);
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
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<TravelBlog> blogPage = new PageImpl<>(List.of(blog), pageable, 1);
        when(travelBlogRepo.findApprovedBlogs(TravelBlog.BlogStatus.APPROVED, pageable)).thenReturn(blogPage);
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

        // When
        PageResult<BlogDto> result = blogService.getAllBlogs(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().title()).isEqualTo("Amazing Yangon");
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.first()).isTrue();
        assertThat(result.last()).isTrue();
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
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

        // When
        BlogDto result = blogService.updateBlog(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        verify(travelBlogRepo).findByIdAndDeletedFalse(1L);
        verify(travelBlogRepo).save(any(TravelBlog.class));
        verify(blogLikeRepo).countLikesByBlogId(1L);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentBlog() {
        // Given
        BlogUpdateRequestDto updateRequest = new BlogUpdateRequestDto(
                "Updated Title",
                null, null, null, null, null,
                null, null, null, null, null, 4L,
                null
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
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

        // When
        List<BlogDto> result = blogService.getBlogsByAuthor(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().authorId()).isEqualTo(1L);

        verify(travelBlogRepo).findByAuthorIdAndDeletedFalse(1L);
        verify(blogLikeRepo).countLikesByBlogId(1L);
    }

    @Test
    void shouldGetApprovedBlogs() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<TravelBlog> blogPage = new PageImpl<>(List.of(blog), pageable, 1);
        // Given
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        when(travelBlogRepo.findApprovedBlogs(TravelBlog.BlogStatus.APPROVED, pageable)).thenReturn(blogPage);
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(0L);

        // When
        PageResult<BlogDto> result = blogService.getApprovedBlogs(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().status()).isEqualTo("APPROVED");

        verify(travelBlogRepo).findApprovedBlogs(TravelBlog.BlogStatus.APPROVED, pageable);
    }

    @Test
    void shouldGetFeaturedBlogsWithCursor() {
        // Given
        TravelBlog blog2 = new TravelBlog();
        blog2.setId(2L);
        blog2.setTitle("Blog 2");
        blog2.setStatus(TravelBlog.BlogStatus.APPROVED);
        blog2.setDeleted(false);

        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        Pageable pageable = PageRequest.of(0, 11);
        when(travelBlogRepo.findFeaturedBlogs(TravelBlog.BlogStatus.APPROVED, null, pageable))
                .thenReturn(List.of(blog, blog2));
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(10L);
        when(blogLikeRepo.countLikesByBlogId(2L)).thenReturn(5L);

        // When
        CursorPageResult<BlogDto> result = blogService.getFeaturedBlogs(null, 10, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.pageSize()).isEqualTo(10);

        verify(travelBlogRepo).findFeaturedBlogs(TravelBlog.BlogStatus.APPROVED, null, pageable);
    }

    @Test
    void shouldGetFeaturedBlogsWithNextCursor() {
        // Given
        TravelBlog blog2 = new TravelBlog();
        blog2.setId(2L);
        blog2.setTitle("Blog 2");
        blog2.setStatus(TravelBlog.BlogStatus.APPROVED);
        blog2.setDeleted(false);

        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        Pageable pageable = PageRequest.of(0, 11);
        List<TravelBlog> blogs = List.of(blog, blog2, blog);
        when(travelBlogRepo.findFeaturedBlogs(TravelBlog.BlogStatus.APPROVED, null, pageable))
                .thenReturn(blogs);
        when(blogLikeRepo.countLikesByBlogId(anyLong())).thenReturn(10L);

        // When
        CursorPageResult<BlogDto> result = blogService.getFeaturedBlogs(null, 2, null);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isNotNull();

        verify(travelBlogRepo).findFeaturedBlogs(TravelBlog.BlogStatus.APPROVED, null, pageable);
    }

    @Test
    void shouldMapToDtoWithLikeCount() {
        // Given
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(5L);

        // When
        BlogDto result = blogService.mapToDto(blog);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.likeCount()).isEqualTo(5L);
        assertThat(result.isLiked()).isNull();
        assertThat(result.isFavorited()).isNull();

        verify(blogLikeRepo).countLikesByBlogId(1L);
    }

    @Test
    void shouldMapToDtoWithUserInteraction() {
        // Given
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        when(blogLikeRepo.countLikesByBlogId(1L)).thenReturn(5L);
        when(blogLikeRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(true);
        when(favoriteBlogRepo.existsByUserIdAndBlogId(1L, 1L)).thenReturn(true);

        // When
        BlogDto result = blogService.mapToDto(blog, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.likeCount()).isEqualTo(5L);
        assertThat(result.isLiked()).isTrue();
        assertThat(result.isFavorited()).isTrue();

        verify(blogLikeRepo).countLikesByBlogId(1L);
        verify(blogLikeRepo).existsByUserIdAndBlogId(1L, 1L);
        verify(favoriteBlogRepo).existsByUserIdAndBlogId(1L, 1L);
    }
}




