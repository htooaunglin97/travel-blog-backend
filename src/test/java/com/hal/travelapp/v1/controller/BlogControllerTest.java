package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.*;
import com.hal.travelapp.v1.dto.blog.BlogCreateRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.dto.blog.BlogUpdateRequestDto;
import com.hal.travelapp.v1.entity.domain.*;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.repository.*;
import com.hal.travelapp.v1.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.assertj.core.api.WithAssertions;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BlogControllerTest implements WithAssertions {

    @Autowired
    TestRestTemplate http;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private CityRepo cityRepo;

    @Autowired
    private TravelCategoryRepo travelCategoryRepo;

    @Autowired
    private TravelBlogRepo travelBlogRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private com.hal.travelapp.v1.repository.BlogLikeRepo blogLikeRepo;

    @Autowired
    private com.hal.travelapp.v1.repository.FavoriteBlogRepo favoriteBlogRepo;

    private String authToken;
    private Long userId;
    private Long cityId;
    private Long categoryId;

    @BeforeEach
    void setUp() {
        // Create role
        Role role = roleRepo.findByName(RoleEnum.ROLE_CERTIFIED_USER)
                .orElseGet(() -> {
                    Role newRole = new Role(RoleEnum.ROLE_CERTIFIED_USER);
                    return roleRepo.save(newRole);
                });

        // Create user
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(role);
        user.setPassword("encoded");
        User savedUser = userRepo.save(user);
        userId = savedUser.getId();

        // Generate token for the user
        authToken = jwtTokenProvider.generateToken(savedUser);

        // Create city
        City city = new City();
        city.setName("Yangon");
        City savedCity = cityRepo.save(city);
        cityId = savedCity.getId();

        // Create category
        TravelCategory category = new TravelCategory();
        category.setName("Honeymoon");
        TravelCategory savedCategory = travelCategoryRepo.save(category);
        categoryId = savedCategory.getId();
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);
        return headers;
    }

    @Test
    void shouldCreateBlogAndReturn201() {
        // Given
        BlogCreateRequestDto createRequest = new BlogCreateRequestDto(
                "Amazing Yangon",
                "main.jpg",
                "First paragraph about Yangon",
                "Second paragraph about Yangon",
                "Third paragraph about Yangon",
                "mid1.jpg",
                "mid2.jpg",
                "mid3.jpg",
                "side.jpg",
                cityId,
                1L,
                3L,
                Set.of(categoryId)
        );

        HttpEntity<BlogCreateRequestDto> request = new HttpEntity<>(createRequest, getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<BlogDto>> response = http.exchange(
                "/api/v1/blogs",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BLOG_CREATED");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().title()).isEqualTo("Amazing Yangon");
        assertThat(response.getBody().getData().mainPhotoUrl()).isEqualTo("main.jpg");
    }

    @Test
    void shouldGetBlogByIdAndReturn200() {
        // Given - Create a blog first
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Test Blog");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<BlogDto>> response = http.exchange(
                "/api/v1/blogs/" + savedBlog.getId(),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().id()).isEqualTo(savedBlog.getId());
        assertThat(response.getBody().getData().title()).isEqualTo("Test Blog");
    }

    @Test
    void shouldGetAllBlogsAndReturn200() {
        // Given - Create blogs
        TravelBlog blog1 = new TravelBlog();
        blog1.setTitle("Blog 1");
        blog1.setMainPhotoUrl("main1.jpg");
        blog1.setParagraph1("Para 1");
        blog1.setParagraph2("Para 2");
        blog1.setParagraph3("Para 3");
        blog1.setMidPhoto1Url("mid1.jpg");
        blog1.setMidPhoto2Url("mid2.jpg");
        blog1.setMidPhoto3Url("mid3.jpg");
        blog1.setSidePhotoUrl("side.jpg");
        blog1.setCity(cityRepo.findById(cityId).orElseThrow());
        blog1.setAuthor(userRepo.findById(userId).orElseThrow());
        blog1.setStatus(TravelBlog.BlogStatus.APPROVED);
        travelBlogRepo.save(blog1);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<java.util.List<BlogDto>>> response = http.exchange(
                "/api/v1/blogs",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    void shouldUpdateBlogAndReturn200() {
        // Given - Create a blog first
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Original Title");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        BlogUpdateRequestDto updateRequest = new BlogUpdateRequestDto(
                "Updated Title",
                null, null, null, null, null, null, null, null, null, null, null, null
        );

        HttpEntity<BlogUpdateRequestDto> request = new HttpEntity<>(updateRequest, getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<BlogDto>> response = http.exchange(
                "/api/v1/blogs/" + savedBlog.getId(),
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BLOG_UPDATED");
        assertThat(response.getBody().getData().title()).isEqualTo("Updated Title");
    }

    @Test
    void shouldDeleteBlogAndReturn200() {
        // Given - Create a blog first
        TravelBlog blog = new TravelBlog();
        blog.setTitle("To Delete");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<Void>> response = http.exchange(
                "/api/v1/blogs/" + savedBlog.getId(),
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BLOG_DELETED");
    }

    @Test
    void shouldGetApprovedBlogsAndReturn200() {
        // Given - Create approved blog
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Approved Blog");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        travelBlogRepo.save(blog);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<java.util.List<BlogDto>>> response = http.exchange(
                "/api/v1/blogs/approved",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    void shouldLikeBlogAndReturn200() {
        // Given - Create an approved blog
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Blog to Like");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<com.hal.travelapp.v1.dto.blog.BlogLikeResponseDto>> response = http.exchange(
                "/api/v1/blogs/" + savedBlog.getId() + "/like",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BLOG_LIKED");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().liked()).isTrue();
        assertThat(response.getBody().getData().likeCount()).isGreaterThanOrEqualTo(1L);
    }

    @Test
    void shouldUnlikeBlogAndReturn200() {
        // Given - Create an approved blog and like it first
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Blog to Unlike");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        // Like the blog first
        com.hal.travelapp.v1.entity.domain.BlogLike blogLike = new com.hal.travelapp.v1.entity.domain.BlogLike();
        blogLike.setUser(userRepo.findById(userId).orElseThrow());
        blogLike.setBlog(savedBlog);
        blogLikeRepo.save(blogLike);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<com.hal.travelapp.v1.dto.blog.BlogLikeResponseDto>> response = http.exchange(
                "/api/v1/blogs/" + savedBlog.getId() + "/like",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BLOG_UNLIKED");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().liked()).isFalse();
    }

    @Test
    void shouldAddToFavoritesAndReturn200() {
        // Given - Create an approved blog
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Blog to Favorite");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<com.hal.travelapp.v1.dto.blog.BlogFavoriteResponseDto>> response = http.exchange(
                "/api/v1/blogs/" + savedBlog.getId() + "/favorite",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BLOG_FAVORITED");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().favorited()).isTrue();
    }

    @Test
    void shouldRemoveFromFavoritesAndReturn200() {
        // Given - Create an approved blog and favorite it first
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Blog to Unfavorite");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        // Favorite the blog first
        com.hal.travelapp.v1.entity.domain.FavoriteBlog favoriteBlog = new com.hal.travelapp.v1.entity.domain.FavoriteBlog();
        favoriteBlog.setUser(userRepo.findById(userId).orElseThrow());
        favoriteBlog.setBlog(savedBlog);
        favoriteBlogRepo.save(favoriteBlog);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<com.hal.travelapp.v1.dto.blog.BlogFavoriteResponseDto>> response = http.exchange(
                "/api/v1/blogs/" + savedBlog.getId() + "/favorite",
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("BLOG_UNFAVORITED");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().favorited()).isFalse();
    }

    @Test
    void shouldGetFavoriteBlogsAndReturn200() {
        // Given - Create an approved blog and favorite it
        TravelBlog blog = new TravelBlog();
        blog.setTitle("Favorite Blog");
        blog.setMainPhotoUrl("main.jpg");
        blog.setParagraph1("Para 1");
        blog.setParagraph2("Para 2");
        blog.setParagraph3("Para 3");
        blog.setMidPhoto1Url("mid1.jpg");
        blog.setMidPhoto2Url("mid2.jpg");
        blog.setMidPhoto3Url("mid3.jpg");
        blog.setSidePhotoUrl("side.jpg");
        blog.setCity(cityRepo.findById(cityId).orElseThrow());
        blog.setAuthor(userRepo.findById(userId).orElseThrow());
        blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        TravelBlog savedBlog = travelBlogRepo.save(blog);

        // Favorite the blog
        com.hal.travelapp.v1.entity.domain.FavoriteBlog favoriteBlog = new com.hal.travelapp.v1.entity.domain.FavoriteBlog();
        favoriteBlog.setUser(userRepo.findById(userId).orElseThrow());
        favoriteBlog.setBlog(savedBlog);
        favoriteBlogRepo.save(favoriteBlog);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<PageResult<BlogDto>>> response = http.exchange(
                "/api/v1/blogs/favorites",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("FAVORITE_BLOGS_RETRIEVED");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().content()).isNotEmpty();
    }

    @Test
    void shouldGetFeaturedBlogsAndReturn200() {
        // Given - Create approved blogs with likes
        TravelBlog blog1 = new TravelBlog();
        blog1.setTitle("Popular Blog 1");
        blog1.setMainPhotoUrl("main1.jpg");
        blog1.setParagraph1("Para 1");
        blog1.setParagraph2("Para 2");
        blog1.setParagraph3("Para 3");
        blog1.setMidPhoto1Url("mid1.jpg");
        blog1.setMidPhoto2Url("mid2.jpg");
        blog1.setMidPhoto3Url("mid3.jpg");
        blog1.setSidePhotoUrl("side.jpg");
        blog1.setCity(cityRepo.findById(cityId).orElseThrow());
        blog1.setAuthor(userRepo.findById(userId).orElseThrow());
        blog1.setStatus(TravelBlog.BlogStatus.APPROVED);
        TravelBlog savedBlog1 = travelBlogRepo.save(blog1);

        // Add likes to the blog
        com.hal.travelapp.v1.entity.domain.BlogLike like1 = new com.hal.travelapp.v1.entity.domain.BlogLike();
        like1.setUser(userRepo.findById(userId).orElseThrow());
        like1.setBlog(savedBlog1);
        blogLikeRepo.save(like1);

        HttpEntity<Void> request = new HttpEntity<>(getAuthHeaders());

        // When
        ResponseEntity<ApiSuccess<com.hal.travelapp.v1.dto.CursorPageResult<BlogDto>>> response = http.exchange(
                "/api/v1/blogs/featured?pageSize=10",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("FEATURED_BLOGS_RETRIEVED");
        assertThat(response.getBody().getData()).isNotNull();
    }
}

