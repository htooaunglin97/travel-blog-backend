package com.hal.travelapp.v1.security;

import com.hal.travelapp.v1.entity.domain.Role;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET_KEY = "test-secret-key-that-is-long-enough-for-hmac-sha-256-algorithm-test";
    private static final long EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", EXPIRATION);
    }

    @Test
    void shouldGenerateTokenForUser() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        Role role = new Role(RoleEnum.ROLE_USER);
        user.setRole(role);

        // When
        String token = jwtTokenProvider.generateToken(user);

        // Then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void shouldValidateToken() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        Role role = new Role(RoleEnum.ROLE_USER);
        user.setRole(role);
        String token = jwtTokenProvider.generateToken(user);

        // When
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    void shouldExtractEmailFromToken() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        Role role = new Role(RoleEnum.ROLE_USER);
        user.setRole(role);
        String token = jwtTokenProvider.generateToken(user);

        // When
        String email = jwtTokenProvider.getEmailFromToken(token);

        // Then
        assertThat(email).isEqualTo("test@example.com");
    }

    @Test
    void shouldExtractUserIdFromToken() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        Role role = new Role(RoleEnum.ROLE_USER);
        user.setRole(role);
        String token = jwtTokenProvider.generateToken(user);

        // When
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // Then
        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void shouldExtractRolesFromToken() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        Role role = new Role(RoleEnum.ROLE_USER);
        user.setRole(role);
        String token = jwtTokenProvider.generateToken(user);

        // When
        List<String> roles = jwtTokenProvider.getRolesFromToken(token);

        // Then
        assertThat(roles).containsExactly("ROLE_USER");
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }
}

