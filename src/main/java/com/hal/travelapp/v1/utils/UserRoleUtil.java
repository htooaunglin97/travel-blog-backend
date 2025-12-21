package com.hal.travelapp.v1.utils;

import com.hal.travelapp.v1.entity.domain.Role;
import com.hal.travelapp.v1.entity.domain.User;
import com.hal.travelapp.v1.entity.enums.RoleEnum;
import com.hal.travelapp.v1.repository.RoleRepo;

import java.util.Optional;

public class UserRoleUtil {

    private UserRoleUtil() {
        // Utility class - prevent instantiation
    }

    public static Role getOrCreateRole(RoleRepo roleRepo, RoleEnum roleEnum) {
        return roleRepo.findByName(roleEnum)
                .orElseGet(() -> {
                    Role role = new Role(roleEnum);
                    return roleRepo.save(role);
                });
    }

    public static boolean hasRole(User user, RoleEnum roleEnum) {
        return user != null 
                && user.getRole() != null 
                && user.getRole().getName() == roleEnum;
    }

    public static boolean isAdmin(User user) {
        return hasRole(user, RoleEnum.ROLE_ADMIN);
    }

    public static boolean isCertifiedUser(User user) {
        return hasRole(user, RoleEnum.ROLE_CERTIFIED_USER);
    }

    public static boolean isRegularUser(User user) {
        return hasRole(user, RoleEnum.ROLE_USER);
    }
}

