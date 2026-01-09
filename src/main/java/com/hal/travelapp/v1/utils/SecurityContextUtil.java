package com.hal.travelapp.v1.utils;

import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.repository.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {

    private SecurityContextUtil() {
        // Utility class - prevent instantiation
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }

    public static User getCurrentUser(UserRepo userRepo) {
        String email = getCurrentUserEmail();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public static Long getCurrentUserId(UserRepo userRepo) {
        return getCurrentUser(userRepo).getId();
    }
}



