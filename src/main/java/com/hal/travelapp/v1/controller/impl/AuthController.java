package com.hal.travelapp.v1.controller.impl;

import com.hal.travelapp.v1.controller.AuthApi;
import com.hal.travelapp.v1.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class AuthController  implements AuthApi {
    @Override
    public ResponseEntity<ApiSuccess<LoginResponseDto>> registerUser(@RequestBody @Valid UserSignUpRequestDto req) {
        ApiSuccess<LoginResponseDto> body = new ApiSuccess<>(
                HttpStatus.CREATED,
                "USER_CREATED",
                "You have been registered successfully",
                new LoginResponseDto(
                        new UserDto(1L, "Mike", "micke@gmail.com"),
                        new AccessTokenDto("fake-token", Instant.now())
                )
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
