package com.hal.travelapp.v1.controller.impl;

import com.hal.travelapp.v1.controller.AdminApi;
import com.hal.travelapp.v1.dto.*;
import com.hal.travelapp.v1.dto.blog.BlogDto;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.AdminService;
import com.hal.travelapp.v1.utils.SecurityContextUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminController implements AdminApi {

    private final AdminService adminService;
    private final UserRepo userRepo;

    public AdminController(AdminService adminService, UserRepo userRepo) {
        this.adminService = adminService;
        this.userRepo = userRepo;
    }

    @Override
    public ResponseEntity<ApiSuccess<CertifiedUserRequestDto>> approveOrRejectCertification(@org.springframework.web.bind.annotation.RequestBody @Valid AdminApprovalRequestDto request) {
        Long adminId = SecurityContextUtil.getCurrentUserId(userRepo);
        CertifiedUserRequestDto result = adminService.approveOrRejectCertificationRequest(request, adminId);

        ApiSuccess<CertifiedUserRequestDto> body = new ApiSuccess<>(
                HttpStatus.OK,
                "CERTIFICATION_REQUEST_PROCESSED",
                "Certification request processed successfully",
                result
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<BlogDto>> approveOrRejectBlog(@org.springframework.web.bind.annotation.RequestBody @Valid BlogApprovalRequestDto request) {
        Long adminId = SecurityContextUtil.getCurrentUserId(userRepo);
        BlogDto result = adminService.approveOrRejectBlog(request, adminId);

        ApiSuccess<BlogDto> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOG_PROCESSED",
                "Blog processed successfully",
                result
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<List<CertifiedUserRequestDto>>> getAllCertificationRequests() {
        List<CertifiedUserRequestDto> requests = adminService.getAllCertificationRequests();

        ApiSuccess<List<CertifiedUserRequestDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "REQUESTS_RETRIEVED",
                "Certification requests retrieved successfully",
                requests
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<List<BlogDto>>> getAllPendingBlogs() {
        List<BlogDto> blogs = adminService.getAllPendingBlogs();

        ApiSuccess<List<BlogDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "BLOGS_RETRIEVED",
                "Pending blogs retrieved successfully",
                blogs
        );

        return ResponseEntity.ok(body);
    }
}



