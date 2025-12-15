package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.auth.LoginRequestDto;
import com.hal.travelapp.v1.dto.auth.LoginResponseDto;
import com.hal.travelapp.v1.dto.auth.UserSignUpRequestDto;

public interface AuthService {
    LoginResponseDto registerUser(UserSignUpRequestDto signUpRequest);

    LoginResponseDto login(LoginRequestDto loginRequest);

}

