package com.hal.travelapp.v1.controller.impl;

import com.hal.travelapp.v1.controller.AuthApi;
import com.hal.travelapp.v1.dto.*;
import com.hal.travelapp.v1.dto.auth.LoginRequestDto;
import com.hal.travelapp.v1.dto.auth.LoginResponseDto;
import com.hal.travelapp.v1.dto.auth.UserSignUpRequestDto;
import com.hal.travelapp.v1.service.AuthService;
import com.hal.travelapp.v1.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    public AuthController(AuthService authService,
                          CookieUtil cookieUtil) {
        this.authService = authService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public ResponseEntity<ApiSuccess<LoginResponseDto>> registerUser(@RequestBody @Valid UserSignUpRequestDto req,
                                                                     HttpServletResponse response) {
        LoginResponseDto loginResponse = authService.registerUser(req);

        cookieUtil.addAccessTokenCookies(response, loginResponse.tokenData().accessToken(),
                loginResponse.tokenData().expiresAt());

        
        ApiSuccess<LoginResponseDto> body = new ApiSuccess<>(
                HttpStatus.CREATED,
                "USER_CREATED",
                "You have been registered successfully",
                loginResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Override
    public ResponseEntity<ApiSuccess<LoginResponseDto>> login(@RequestBody @Valid LoginRequestDto req,
                                                              HttpServletResponse response) {
        LoginResponseDto loginResponse = authService.login(req);

        cookieUtil.addAccessTokenCookies(response, loginResponse.tokenData().accessToken(),
                loginResponse.tokenData().expiresAt());
        
        ApiSuccess<LoginResponseDto> body = new ApiSuccess<>(
                HttpStatus.OK,
                "LOGIN_SUCCESS",
                "Login successful",
                loginResponse
        );

        return ResponseEntity.ok(body);
    }
}
