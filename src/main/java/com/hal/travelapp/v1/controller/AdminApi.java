package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/admin")
public interface AdminApi {

    @PostMapping("/certification/approve")
    ResponseEntity<ApiSuccess<CertifiedUserRequestDto>> approveOrRejectCertification(@RequestBody AdminApprovalRequestDto request);

    @PostMapping("/blog/approve")
    ResponseEntity<ApiSuccess<BlogDto>> approveOrRejectBlog(@RequestBody BlogApprovalRequestDto request);

    @GetMapping("/certification/requests")
    ResponseEntity<ApiSuccess<List<CertifiedUserRequestDto>>> getAllCertificationRequests();

    @GetMapping("/blog/pending")
    ResponseEntity<ApiSuccess<List<BlogDto>>> getAllPendingBlogs();
}

