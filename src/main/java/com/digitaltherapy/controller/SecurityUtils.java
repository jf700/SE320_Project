package com.digitaltherapy.controller;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

final class SecurityUtils {

    private SecurityUtils() {}

    static UUID currentUserId() {
        return UUID.fromString(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
    }
}
