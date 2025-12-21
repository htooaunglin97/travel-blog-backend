package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.dto.AdminApprovalRequestDto;
import com.hal.travelapp.v1.dto.BlogApprovalRequestDto;
import com.hal.travelapp.v1.dto.BlogDto;
import com.hal.travelapp.v1.dto.CertifiedUserRequestDto;
import com.hal.travelapp.v1.entity.domain.*;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.exception.ResourceNotFoundException;
import com.hal.travelapp.v1.repository.*;
import com.hal.travelapp.v1.exception.InvalidActionException;
import com.hal.travelapp.v1.service.AdminService;
import com.hal.travelapp.v1.service.BlogService;
import com.hal.travelapp.v1.utils.UserRoleUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final CertifiedUserRequestRepo certifiedUserRequestRepo;
    private final TravelBlogRepo travelBlogRepo;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final BlogService blogService;

    public AdminServiceImpl(CertifiedUserRequestRepo certifiedUserRequestRepo,
                           TravelBlogRepo travelBlogRepo,
                           UserRepo userRepo,
                           RoleRepo roleRepo,
                           BlogService blogService) {
        this.certifiedUserRequestRepo = certifiedUserRequestRepo;
        this.travelBlogRepo = travelBlogRepo;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.blogService = blogService;
    }

    @Override
    public CertifiedUserRequestDto approveOrRejectCertificationRequest(AdminApprovalRequestDto request, Long adminId) {
        // Get the certification request
        CertifiedUserRequest certificationRequest = certifiedUserRequestRepo.findById(request.requestId())
                .orElseThrow(() -> new ResourceNotFoundException("Certification request not found with id: " + request.requestId()));

        // Get admin user
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        // Update request status
        if ("APPROVE".equalsIgnoreCase(request.action())) {
            certificationRequest.setStatus(CertifiedUserRequest.RequestStatus.APPROVED);
            certificationRequest.setReviewedBy(admin);

            // Update user role to CERTIFIED_USER
            User user = certificationRequest.getUser();
            Role certifiedRole = UserRoleUtil.getOrCreateRole(roleRepo, RoleEnum.ROLE_CERTIFIED_USER);
            user.setRole(certifiedRole);
            userRepo.save(user);
        } else if ("REJECT".equalsIgnoreCase(request.action())) {
            certificationRequest.setStatus(CertifiedUserRequest.RequestStatus.REJECTED);
            certificationRequest.setReviewedBy(admin);
            certificationRequest.setRejectionReason(request.rejectionReason());
        } else {
            throw new InvalidActionException("Invalid action. Must be APPROVE or REJECT");
        }

        CertifiedUserRequest savedRequest = certifiedUserRequestRepo.save(certificationRequest);
        return mapCertificationRequestToDto(savedRequest);
    }

    @Override
    public BlogDto approveOrRejectBlog(BlogApprovalRequestDto request, Long adminId) {
        // Get the blog
        TravelBlog blog = travelBlogRepo.findById(request.blogId())
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with id: " + request.blogId()));

        // Get admin user
        User admin = userRepo.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        // Update blog status
        if ("APPROVE".equalsIgnoreCase(request.action())) {
            blog.setStatus(TravelBlog.BlogStatus.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(request.action())) {
            blog.setStatus(TravelBlog.BlogStatus.REJECTED);
        } else {
            throw new InvalidActionException("Invalid action. Must be APPROVE or REJECT");
        }

        TravelBlog savedBlog = travelBlogRepo.save(blog);
        return blogService.mapToDto(savedBlog);
    }

    @Override
    public List<CertifiedUserRequestDto> getAllCertificationRequests() {
        return certifiedUserRequestRepo.findAll()
                .stream()
                .filter(request -> !request.isDeleted())
                .map(this::mapCertificationRequestToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogDto> getAllPendingBlogs() {
        return travelBlogRepo.findByStatusAndDeletedFalse(TravelBlog.BlogStatus.PENDING)
                .stream()
                .map(blogService::mapToDto)
                .collect(Collectors.toList());
    }

    private CertifiedUserRequestDto mapCertificationRequestToDto(CertifiedUserRequest request) {
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

