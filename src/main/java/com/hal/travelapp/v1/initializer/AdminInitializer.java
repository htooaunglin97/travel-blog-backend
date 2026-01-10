package com.hal.travelapp.v1.initializer;

import com.hal.travelapp.v1.entity.domain.Role;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.repository.RoleRepo;
import com.hal.travelapp.v1.repository.UserRepo;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static com.hal.travelapp.v1.entity.enums.RoleEnum.ROLE_ADMIN;

@Component
@RequiredArgsConstructor
@Order(1)
public class AdminInitializer implements CommandLineRunner {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(@Nullable  String... args) throws Exception {

        Role role = new Role();
        role.setName(ROLE_ADMIN);

        roleRepo.save(role);
        roleRepo.flush();

        Role adminRole = roleRepo.findByName(ROLE_ADMIN).
                orElseThrow(() -> new IllegalStateException("Admin Role Not Found"));

        if (userRepo.existsByEmail("admin@gmail.com")) {
            return;
        }

        User user = new User();
        user.setEmail("admin@gmail.com");
        user.setName("Admin");
        user.setPassword(passwordEncoder.encode("admin@123"));
        user.setRole(adminRole);

        userRepo.save(user);
    }
}
