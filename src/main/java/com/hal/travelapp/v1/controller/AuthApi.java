package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.ApiSuccess;
import com.hal.travelapp.v1.dto.LoginRequestDto;
import com.hal.travelapp.v1.dto.LoginResponseDto;
import com.hal.travelapp.v1.dto.UserSignUpRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/auth")
public interface AuthApi {

    @PostMapping("/register")
    ResponseEntity<ApiSuccess<LoginResponseDto>> registerUser(UserSignUpRequestDto req);

    @PostMapping("/login")
    ResponseEntity<ApiSuccess<LoginResponseDto>> login(LoginRequestDto req);
}
