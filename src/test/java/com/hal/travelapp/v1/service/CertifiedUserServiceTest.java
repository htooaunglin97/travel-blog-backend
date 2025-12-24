package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.CertifiedUserRequestDto;
import com.hal.travelapp.v1.dto.CertifiedUserRequestResponseDto;
import com.hal.travelapp.v1.entity.domain.CertifiedUserRequest;
import com.hal.travelapp.v1.entity.domain.Role;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.entity.enums.RequestStatus;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.CertifiedUserRequestRepo;
import com.hal.travelapp.v1.repository.RoleRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.impl.CertifiedUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertifiedUserServiceTest {

    @Mock
    private CertifiedUserRequestRepo certifiedUserRequestRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private CertifiedUserServiceImpl certifiedUserService;

    private User regularUser;
    private CertifiedUserRequest pendingRequest;

    @BeforeEach
    void setUp() {
        Role userRole = new Role(RoleEnum.ROLE_USER);
        userRole.setId(1L);

        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setName("John Doe");
        regularUser.setEmail("john@example.com");
        regularUser.setRole(userRole);

        pendingRequest = new CertifiedUserRequest();
        pendingRequest.setId(1L);
        pendingRequest.setUser(regularUser);
        pendingRequest.setStatus(RequestStatus.PENDING);
        pendingRequest.setCreatedAt(Instant.now());
        pendingRequest.setUpdatedAt(Instant.now());
        pendingRequest.setDeleted(false);
    }

    @Test
    void shouldCreateCertificationRequest() {
        // Given
        when(userRepo.findById(1L)).thenReturn(Optional.of(regularUser));
        when(certifiedUserRequestRepo.findByUserIdAndStatus(1L, RequestStatus.PENDING))
                .thenReturn(Optional.empty());
        when(certifiedUserRequestRepo.save(any(CertifiedUserRequest.class))).thenAnswer(invocation -> {
            CertifiedUserRequest request = invocation.getArgument(0);
            request.setId(1L);
            return request;
        });

        // When
        CertifiedUserRequestResponseDto result = certifiedUserService.requestCertification(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("PENDING");
        assertThat(result.requestId()).isEqualTo(1L);
        assertThat(result.message()).contains("successfully");

        verify(userRepo).findById(1L);
        verify(certifiedUserRequestRepo).findByUserIdAndStatus(1L, RequestStatus.PENDING);
        verify(certifiedUserRequestRepo).save(any(CertifiedUserRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepo.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> certifiedUserService.requestCertification(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepo).findById(999L);
        verify(certifiedUserRequestRepo, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPendingRequestAlreadyExists() {
        // Given
        when(userRepo.findById(1L)).thenReturn(Optional.of(regularUser));
        when(certifiedUserRequestRepo.findByUserIdAndStatus(1L, RequestStatus.PENDING))
                .thenReturn(Optional.of(pendingRequest));

        // When/Then
        assertThatThrownBy(() -> certifiedUserService.requestCertification(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");

        verify(userRepo).findById(1L);
        verify(certifiedUserRequestRepo).findByUserIdAndStatus(1L, RequestStatus.PENDING);
        verify(certifiedUserRequestRepo, never()).save(any());
    }

    @Test
    void shouldGetRequestByUserId() {
        // Given
        when(certifiedUserRequestRepo.findByUserIdAndDeletedFalse(1L))
                .thenReturn(Optional.of(pendingRequest));

        // When
        CertifiedUserRequestDto result = certifiedUserService.getRequestByUserId(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo("PENDING");

        verify(certifiedUserRequestRepo).findByUserIdAndDeletedFalse(1L);
    }

    @Test
    void shouldThrowExceptionWhenRequestNotFound() {
        // Given
        when(certifiedUserRequestRepo.findByUserIdAndDeletedFalse(999L))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> certifiedUserService.getRequestByUserId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Request not found");

        verify(certifiedUserRequestRepo).findByUserIdAndDeletedFalse(999L);
    }

    @Test
    void shouldGetAllPendingRequests() {
        // Given
        when(certifiedUserRequestRepo.findByStatusAndDeletedFalse(RequestStatus.PENDING))
                .thenReturn(List.of(pendingRequest));

        // When
        List<CertifiedUserRequestDto> result = certifiedUserService.getAllPendingRequests();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo("PENDING");

        verify(certifiedUserRequestRepo).findByStatusAndDeletedFalse(RequestStatus.PENDING);
    }
}

