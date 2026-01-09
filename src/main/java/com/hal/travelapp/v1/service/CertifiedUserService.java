package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.CertifiedUserRequestDto;
import com.hal.travelapp.v1.dto.CertifiedUserRequestResponseDto;

import java.util.List;

public interface CertifiedUserService {
    CertifiedUserRequestResponseDto requestCertification(Long userId);
    
    CertifiedUserRequestDto getRequestByUserId(Long userId);
    
    List<CertifiedUserRequestDto> getAllPendingRequests();
}



