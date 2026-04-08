package com.digitaltherapy.security;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token blacklist for logout invalidation.
 * In production, replace with Redis TTL store.
 */
@Component
public class TokenBlacklist {

    private final Set<String> blacklisted = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void add(String token) {
        blacklisted.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklisted.contains(token);
    }
}
