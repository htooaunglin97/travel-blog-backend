package com.hal.travelapp.v1.controller.impl;

import com.hal.travelapp.v1.controller.AuthApi;
import com.hal.travelapp.v1.dto.*;
import com.hal.travelapp.v1.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public ResponseEntity<ApiSuccess<LoginResponseDto>> registerUser(@RequestBody @Valid UserSignUpRequestDto req) {
        LoginResponseDto loginResponse = authService.registerUser(req);
        
        ApiSuccess<LoginResponseDto> body = new ApiSuccess<>(
                HttpStatus.CREATED,
                "USER_CREATED",
                "You have been registered successfully",
                loginResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto req) {
        LoginResponseDto loginResponse = authService.login(req);
        
        ApiSuccess<LoginResponseDto> body = new ApiSuccess<>(
                HttpStatus.OK,
                "LOGIN_SUCCESS",
                "Login successful",
                loginResponse
        );

        return ResponseEntity.ok(body);
    }
}
