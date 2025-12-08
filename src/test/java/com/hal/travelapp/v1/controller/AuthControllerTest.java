package com.hal.travelapp.v1.controller;

import com.hal.travelapp.v1.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.assertj.core.api.WithAssertions;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class AuthControllerTest implements WithAssertions
{

    @Autowired
    TestRestTemplate http;


    @Test
    void shouldSignUpUserAndReturn201WithJwtTest() {
        ResponseEntity<ApiSuccess<LoginResponseDto>> rsp =
                http.exchange("/api/v1/auth/register",
                        HttpMethod.POST,
                        new HttpEntity<>(new UserSignUpRequestDto("Mike", "mike@gmail.com", "password123")),
                        new ParameterizedTypeReference<>() {});

        assertThat(rsp.getBody()).isNotNull();
        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ApiSuccess<LoginResponseDto> body = rsp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo("USER_CREATED");
        assertThat(body.getMessage()).isEqualTo("You have been registered successfully");
        assertThat(body.getData()).isNotNull();
        assertThat(body.getData().userData().email()).isEqualTo("mike@gmail.com");
        assertThat(body.getData().userData().name()).isEqualTo("Mike");
        assertThat(body.getData().tokenData().accessToken()).isNotNull();
        assertThat(body.getData().tokenData().expiresAt()).isNotNull();
    }

    @Test
    void shouldLoginUserAndReturn200WithJwtTest() {
        // First register a user
        http.exchange("/api/v1/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(new UserSignUpRequestDto("John", "john@example.com", "password123")),
                new ParameterizedTypeReference<ApiSuccess<LoginResponseDto>>() {});

        // Then login
        ResponseEntity<ApiSuccess<LoginResponseDto>> rsp =
                http.exchange("/api/v1/auth/login",
                        HttpMethod.POST,
                        new HttpEntity<>(new LoginRequestDto("john@example.com", "password123")),
                        new ParameterizedTypeReference<>() {});

        assertThat(rsp.getBody()).isNotNull();
        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.OK);

        ApiSuccess<LoginResponseDto> body = rsp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getCode()).isEqualTo("LOGIN_SUCCESS");
        assertThat(body.getMessage()).isEqualTo("Login successful");
        assertThat(body.getData()).isNotNull();
        assertThat(body.getData().userData().email()).isEqualTo("john@example.com");
        assertThat(body.getData().userData().name()).isEqualTo("John");
        assertThat(body.getData().tokenData().accessToken()).isNotNull();
        assertThat(body.getData().tokenData().expiresAt()).isNotNull();
    }
}
