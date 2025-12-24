package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.dto.CertifiedUserRequestDto;
import com.hal.travelapp.v1.dto.CertifiedUserRequestResponseDto;
import com.hal.travelapp.v1.entity.domain.CertifiedUserRequest;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.entity.enums.RequestStatus;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.CertifiedUserRequestRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.CertifiedUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CertifiedUserServiceImpl implements CertifiedUserService {

    private final CertifiedUserRequestRepo certifiedUserRequestRepo;
    private final UserRepo userRepo;

    public CertifiedUserServiceImpl(CertifiedUserRequestRepo certifiedUserRequestRepo, UserRepo userRepo) {
        this.certifiedUserRequestRepo = certifiedUserRequestRepo;
        this.userRepo = userRepo;
    }

    @Override
    public CertifiedUserRequestResponseDto requestCertification(Long userId) {
        // Validate user exists
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if there's already a pending request
        if (certifiedUserRequestRepo.findByUserIdAndStatus(userId, RequestStatus.PENDING)
                .isPresent()) {
            throw new RuntimeException("A pending certification request already exists for this user");
        }

        // Create new request
        CertifiedUserRequest request = new CertifiedUserRequest();
        request.setUser(user);
        request.setStatus(RequestStatus.PENDING);

        CertifiedUserRequest savedRequest = certifiedUserRequestRepo.save(request);

        return new CertifiedUserRequestResponseDto(
                savedRequest.getId(),
                "PENDING",
                "Certification request submitted successfully. Waiting for admin approval."
        );
    }

    @Override
    public CertifiedUserRequestDto getRequestByUserId(Long userId) {
        CertifiedUserRequest request = certifiedUserRequestRepo.findByUserIdAndDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification request not found for user id: " + userId));

        return mapToDto(request);
    }

    @Override
    public List<CertifiedUserRequestDto> getAllPendingRequests() {
        return certifiedUserRequestRepo.findByStatusAndDeletedFalse(RequestStatus.PENDING)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private CertifiedUserRequestDto mapToDto(CertifiedUserRequest request) {
        return new CertifiedUserRequestDto(
                request.getId(),
                request.getUser().getId(),
                request.getUser().getName(),
                request.getUser().getEmail(),
                request.getStatus().name(),
                request.getReviewedBy() != null ? request.getReviewedBy().getId() : null,
                request.getReviewedBy() != null ? request.getReviewedBy().getName() : null,
                request.getRejectionReason(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}

