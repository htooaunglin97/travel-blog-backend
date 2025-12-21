package com.hal.travelapp.v1.service.impl;

import com.hal.travelapp.v1.dto.auth.LoginRequestDto;
import com.hal.travelapp.v1.dto.auth.LoginResponseDto;
import com.hal.travelapp.v1.dto.auth.UserSignUpRequestDto;
import com.hal.travelapp.v1.dto.token.AccessTokenDto;
import com.hal.travelapp.v1.dto.user.UserDto;
import com.hal.travelapp.v1.entity.domain.Role;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.exception.EmailAlreadyExistsException;
import com.hal.travelapp.v1.exception.InvalidCredentialsException;
import com.hal.travelapp.v1.repository.RoleRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import com.hal.travelapp.v1.security.JwtTokenProvider;
import com.hal.travelapp.v1.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepo userRepo, RoleRepo roleRepo, PasswordEncoder passwordEncoder,
                        JwtTokenProvider jwtTokenProvider) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponseDto registerUser(UserSignUpRequestDto signUpRequest) {
        // Check if email already exists
        if (userRepo.findByEmail(signUpRequest.email()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Get or create ROLE_USER
        Role userRole = roleRepo.findByName(RoleEnum.ROLE_USER)
                .orElseGet(() -> {
                    Role role = new Role(RoleEnum.ROLE_USER);
                    return roleRepo.save(role);
                });

        // Create new user
        User user = new User();
        user.setName(signUpRequest.name());
        user.setEmail(signUpRequest.email());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setRole(userRole);

        User savedUser = userRepo.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedUser);
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);


        return new LoginResponseDto(
                new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail()),
                new AccessTokenDto(token, expirationDate.toInstant())
        );
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest) {
        // Find user by email
        User user = userRepo.findByEmail(loginRequest.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);
        Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);

        return new LoginResponseDto(
                new UserDto(user.getId(), user.getName(), user.getEmail()),
                new AccessTokenDto(token, expirationDate.toInstant())
        );
    }
}
