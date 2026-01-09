package com.hal.travelapp.v1.controller.impl;

import com.hal.travelapp.v1.controller.CertifiedUserApi;
import com.hal.travelapp.v1.dto.*;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.service.CertifiedUserService;
import com.hal.travelapp.v1.utils.SecurityContextUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CertifiedUserController implements CertifiedUserApi {

    private final CertifiedUserService certifiedUserService;
    private final UserRepo userRepo;

    public CertifiedUserController(CertifiedUserService certifiedUserService, UserRepo userRepo) {
        this.certifiedUserService = certifiedUserService;
        this.userRepo = userRepo;
    }

    @Override
    public ResponseEntity<ApiSuccess<CertifiedUserRequestResponseDto>> requestCertification() {
        Long userId = SecurityContextUtil.getCurrentUserId(userRepo);
        CertifiedUserRequestResponseDto response = certifiedUserService.requestCertification(userId);

        ApiSuccess<CertifiedUserRequestResponseDto> body = new ApiSuccess<>(
                HttpStatus.CREATED,
                "CERTIFICATION_REQUEST_CREATED",
                "Certification request submitted successfully",
                response
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<CertifiedUserRequestDto>> getMyRequest() {
        Long userId = SecurityContextUtil.getCurrentUserId(userRepo);
        CertifiedUserRequestDto request = certifiedUserService.getRequestByUserId(userId);

        ApiSuccess<CertifiedUserRequestDto> body = new ApiSuccess<>(
                HttpStatus.OK,
                "REQUEST_FOUND",
                "Request retrieved successfully",
                request
        );

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<List<CertifiedUserRequestDto>>> getAllPendingRequests() {
        List<CertifiedUserRequestDto> requests = certifiedUserService.getAllPendingRequests();

        ApiSuccess<List<CertifiedUserRequestDto>> body = new ApiSuccess<>(
                HttpStatus.OK,
                "REQUESTS_RETRIEVED",
                "Pending requests retrieved successfully",
                requests
        );

        return ResponseEntity.ok(body);
    }
}



