package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.LoginRequestDto;
import com.hal.travelapp.v1.dto.LoginResponseDto;
import com.hal.travelapp.v1.dto.UserSignUpRequestDto;

public interface AuthService {
    LoginResponseDto registerUser(UserSignUpRequestDto signUpRequest);

    LoginResponseDto login(LoginRequestDto loginRequest);

}

