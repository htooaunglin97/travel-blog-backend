package com.hal.travelapp.v1.service;

import com.hal.travelapp.v1.dto.auth.LoginRequestDto;
import com.hal.travelapp.v1.dto.auth.LoginResponseDto;
import com.hal.travelapp.v1.dto.auth.UserSignUpRequestDto;
import com.hal.travelapp.v1.entity.domain.Role;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.exception.EmailAlreadyExistsException;
import com.hal.travelapp.v1.exception.InvalidCredentialsException;
import com.hal.travelapp.v1.repository.RoleRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.security.JwtTokenProvider;
import com.hal.travelapp.v1.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role userRole;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userRole = new Role(RoleEnum.ROLE_USER);
        userRole.setId(1L);

        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("John Doe");
        existingUser.setEmail("john@example.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setRole(userRole);
    }

    @Test
    void shouldRegisterNewUser() {
        // Given
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto(
                "John Doe",
                "john@example.com",
                "password123"
        );

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(roleRepo.findByName(RoleEnum.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("test-token");
        when(jwtTokenProvider.getExpirationDateFromToken(anyString())).thenReturn(new java.util.Date(System.currentTimeMillis() + 86400000));

        // When
        LoginResponseDto response = authService.registerUser(signUpRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userData().email()).isEqualTo("john@example.com");
        assertThat(response.userData().name()).isEqualTo("John Doe");
        assertThat(response.tokenData().accessToken()).isEqualTo("test-token");
        assertThat(response.tokenData().expiresAt()).isNotNull();

        verify(userRepo).findByEmail("john@example.com");
        verify(roleRepo).findByName(RoleEnum.ROLE_USER);
        verify(passwordEncoder).encode("password123");
        verify(userRepo).save(any(User.class));
        verify(jwtTokenProvider).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto(
                "John Doe",
                "john@example.com",
                "password123"
        );

        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));

        // When/Then
        assertThatThrownBy(() -> authService.registerUser(signUpRequest))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists");

        verify(userRepo).findByEmail("john@example.com");
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void shouldLoginUserWithValidCredentials() {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto(
                "john@example.com",
                "password123"
        );

        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(existingUser)).thenReturn("test-token");
        when(jwtTokenProvider.getExpirationDateFromToken("test-token")).thenReturn(new java.util.Date(System.currentTimeMillis() + 86400000));

        // When
        LoginResponseDto response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.userData().email()).isEqualTo("john@example.com");
        assertThat(response.userData().name()).isEqualTo("John Doe");
        assertThat(response.tokenData().accessToken()).isEqualTo("test-token");

        verify(userRepo).findByEmail("john@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtTokenProvider).generateToken(existingUser);
    }

    @Test
    void shouldThrowExceptionWhenEmailNotFound() {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto(
                "nonexistent@example.com",
                "password123"
        );

        when(userRepo.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");

        verify(userRepo).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsIncorrect() {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto(
                "john@example.com",
                "wrongPassword"
        );

        when(userRepo.findByEmail("john@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("Invalid email or password");

        verify(userRepo).findByEmail("john@example.com");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(jwtTokenProvider, never()).generateToken(any(User.class));
    }
}

