package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.AdminApprovalRequestDto;
import com.hal.travelapp.v1.dto.BlogApprovalRequestDto;
import com.hal.travelapp.v1.dto.BlogDto;
import com.hal.travelapp.v1.dto.CertifiedUserRequestDto;
import com.hal.travelapp.v1.entity.domain.*;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.*;
import com.hal.travelapp.v1.service.impl.AdminServiceImpl;
import com.hal.travelapp.v1.utils.UserRoleUtil;
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
class AdminServiceTest {

    @Mock
    private CertifiedUserRequestRepo certifiedUserRequestRepo;

    @Mock
    private TravelBlogRepo travelBlogRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User adminUser;
    private User regularUser;
    private User certifiedUser;
    private Role adminRole;
    private Role userRole;
    private Role certifiedRole;
    private CertifiedUserRequest pendingRequest;
    private TravelBlog pendingBlog;

    @BeforeEach
    void setUp() {
        adminRole = new Role(RoleEnum.ROLE_ADMIN);
        adminRole.setId(1L);

        userRole = new Role(RoleEnum.ROLE_USER);
        userRole.setId(2L);

        certifiedRole = new Role(RoleEnum.ROLE_CERTIFIED_USER);
        certifiedRole.setId(3L);

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole(adminRole);

        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setName("Regular User");
        regularUser.setEmail("regular@example.com");
        regularUser.setRole(userRole);

        certifiedUser = new User();
        certifiedUser.setId(3L);
        certifiedUser.setName("Certified User");
        certifiedUser.setEmail("certified@example.com");
        certifiedUser.setRole(certifiedRole);

        pendingRequest = new CertifiedUserRequest();
        pendingRequest.setId(1L);
        pendingRequest.setUser(regularUser);
        pendingRequest.setStatus(CertifiedUserRequest.RequestStatus.PENDING);
        pendingRequest.setCreatedAt(Instant.now());
        pendingRequest.setUpdatedAt(Instant.now());
        pendingRequest.setDeleted(false);

        City city = new City();
        city.setId(1L);
        city.setName("Yangon");

        pendingBlog = new TravelBlog();
        pendingBlog.setId(1L);
        pendingBlog.setTitle("Test Blog");
        pendingBlog.setMainPhotoUrl("main.jpg");
        pendingBlog.setParagraph1("Para 1");
        pendingBlog.setParagraph2("Para 2");
        pendingBlog.setParagraph3("Para 3");
        pendingBlog.setMidPhoto1Url("mid1.jpg");
        pendingBlog.setMidPhoto2Url("mid2.jpg");
        pendingBlog.setMidPhoto3Url("mid3.jpg");
        pendingBlog.setSidePhotoUrl("side.jpg");
        pendingBlog.setCity(city);
        pendingBlog.setAuthor(certifiedUser);
        pendingBlog.setStatus(TravelBlog.BlogStatus.PENDING);
        pendingBlog.setCreatedAt(Instant.now());
        pendingBlog.setUpdatedAt(Instant.now());
        pendingBlog.setDeleted(false);
    }

    @Test
    void shouldApproveCertificationRequest() {
        // Given
        AdminApprovalRequestDto approvalRequest = new AdminApprovalRequestDto(1L, "APPROVE", null);

        when(certifiedUserRequestRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(userRepo.findById(1L)).thenReturn(Optional.of(adminUser));
        when(userRepo.findById(2L)).thenReturn(Optional.of(regularUser));
        when(roleRepo.findByName(RoleEnum.ROLE_CERTIFIED_USER)).thenReturn(Optional.of(certifiedRole));
        when(certifiedUserRequestRepo.save(any(CertifiedUserRequest.class))).thenReturn(pendingRequest);
        when(userRepo.save(any(User.class))).thenReturn(regularUser);

        // When
        CertifiedUserRequestDto result = adminService.approveOrRejectCertificationRequest(approvalRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("APPROVED");
        assertThat(result.reviewedById()).isEqualTo(1L);

        verify(certifiedUserRequestRepo).findById(1L);
        verify(userRepo).findById(2L);
        verify(roleRepo).findByName(RoleEnum.ROLE_CERTIFIED_USER);
        verify(userRepo).save(regularUser);
        verify(certifiedUserRequestRepo).save(any(CertifiedUserRequest.class));
    }

    @Test
    void shouldRejectCertificationRequest() {
        // Given
        AdminApprovalRequestDto rejectionRequest = new AdminApprovalRequestDto(1L, "REJECT", "Insufficient credentials");

        when(certifiedUserRequestRepo.findById(1L)).thenReturn(Optional.of(pendingRequest));
        when(userRepo.findById(1L)).thenReturn(Optional.of(adminUser));
        when(certifiedUserRequestRepo.save(any(CertifiedUserRequest.class))).thenReturn(pendingRequest);

        // When
        CertifiedUserRequestDto result = adminService.approveOrRejectCertificationRequest(rejectionRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("REJECTED");
        assertThat(result.rejectionReason()).isEqualTo("Insufficient credentials");

        verify(certifiedUserRequestRepo).findById(1L);
        verify(userRepo).findById(1L);
        verify(certifiedUserRequestRepo).save(any(CertifiedUserRequest.class));
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenCertificationRequestNotFound() {
        // Given
        AdminApprovalRequestDto approvalRequest = new AdminApprovalRequestDto(999L, "APPROVE", null);

        when(certifiedUserRequestRepo.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> adminService.approveOrRejectCertificationRequest(approvalRequest, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Certification request not found");

        verify(certifiedUserRequestRepo).findById(999L);
    }

    @Test
    void shouldApproveBlog() {
        // Given
        BlogApprovalRequestDto approvalRequest = new BlogApprovalRequestDto(1L, "APPROVE", null);

        when(travelBlogRepo.findById(1L)).thenReturn(Optional.of(pendingBlog));
        when(userRepo.findById(1L)).thenReturn(Optional.of(adminUser));
        when(travelBlogRepo.save(any(TravelBlog.class))).thenReturn(pendingBlog);

        // When
        BlogDto result = adminService.approveOrRejectBlog(approvalRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("APPROVED");

        verify(travelBlogRepo).findById(1L);
        verify(userRepo).findById(1L);
        verify(travelBlogRepo).save(any(TravelBlog.class));
    }

    @Test
    void shouldRejectBlog() {
        // Given
        BlogApprovalRequestDto rejectionRequest = new BlogApprovalRequestDto(1L, "REJECT", "Content violates guidelines");

        when(travelBlogRepo.findById(1L)).thenReturn(Optional.of(pendingBlog));
        when(userRepo.findById(1L)).thenReturn(Optional.of(adminUser));
        when(travelBlogRepo.save(any(TravelBlog.class))).thenReturn(pendingBlog);

        // When
        BlogDto result = adminService.approveOrRejectBlog(rejectionRequest, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("REJECTED");

        verify(travelBlogRepo).findById(1L);
        verify(userRepo).findById(1L);
        verify(travelBlogRepo).save(any(TravelBlog.class));
    }

    @Test
    void shouldGetAllPendingBlogs() {
        // Given
        when(travelBlogRepo.findByStatusAndDeletedFalse(TravelBlog.BlogStatus.PENDING))
                .thenReturn(List.of(pendingBlog));

        // When
        List<BlogDto> result = adminService.getAllPendingBlogs();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo("PENDING");

        verify(travelBlogRepo).findByStatusAndDeletedFalse(TravelBlog.BlogStatus.PENDING);
    }
}

