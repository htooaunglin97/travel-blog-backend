package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.AdminApprovalRequestDto;
import com.hal.travelapp.v1.dto.BlogApprovalRequestDto;
import com.hal.travelapp.v1.dto.CertifiedUserRequestDto;
import com.hal.travelapp.v1.dto.blog.BlogDto;

import java.util.List;

public interface AdminService {
    CertifiedUserRequestDto approveOrRejectCertificationRequest(AdminApprovalRequestDto request, Long adminId);
    
    BlogDto approveOrRejectBlog(BlogApprovalRequestDto request, Long adminId);
    
    List<CertifiedUserRequestDto> getAllCertificationRequests();
    
    List<BlogDto> getAllPendingBlogs();
}

