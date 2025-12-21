package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.ApiSuccess;
import com.hal.travelapp.v1.dto.CertifiedUserRequestDto;
import com.hal.travelapp.v1.dto.CertifiedUserRequestResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/certified")
public interface CertifiedUserApi {

    @PostMapping("/request")
    ResponseEntity<ApiSuccess<CertifiedUserRequestResponseDto>> requestCertification();

    @GetMapping("/request")
    ResponseEntity<ApiSuccess<CertifiedUserRequestDto>> getMyRequest();

    @GetMapping("/pending")
    ResponseEntity<ApiSuccess<List<CertifiedUserRequestDto>>> getAllPendingRequests();
}

