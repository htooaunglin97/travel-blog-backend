package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.*;
import com.hal.travelapp.v1.dto.auth.LoginRequestDto;
import com.hal.travelapp.v1.dto.auth.LoginResponseDto;
import com.hal.travelapp.v1.dto.auth.UserSignUpRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication Management", description = "api for authentication management")
public interface AuthApi {

    @Operation(summary = "register a new user", description = "add a new user to the system")
    @PostMapping("/register")
    ResponseEntity<ApiSuccess<LoginResponseDto>> registerUser(UserSignUpRequestDto req,
                                                              HttpServletResponse response);


    @PostMapping("/login")
    ResponseEntity<ApiSuccess<LoginResponseDto>> login(LoginRequestDto req,
                                                       HttpServletResponse response);
}
