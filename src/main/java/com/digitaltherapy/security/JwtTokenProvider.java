package com.digitaltherapy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final String secret;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expirationMs,
                            @Value("${jwt.refresh-expiration}") long refreshExpirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(UUID userId, String email) {
        return buildToken(userId.toString(), "access", expirationMs);
    }

    public String generateRefreshToken(UUID userId) {
        return buildToken(userId.toString(), "refresh", refreshExpirationMs);
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(extractField(decodePayload(token), "sub"));
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;
            if (!sign(parts[0] + "." + parts[1]).equals(parts[2])) return false;
            long exp = Long.parseLong(extractField(decodePayload(token), "exp"));
            return System.currentTimeMillis() < exp;
        } catch (Exception e) { log.warn("Invalid JWT: {}", e.getMessage()); return false; }
    }

    public boolean isRefreshToken(String token) {
        try { return "refresh".equals(extractField(decodePayload(token), "type")); }
        catch (Exception e) { return false; }
    }

    private String buildToken(String subject, String type, long ttlMs) {
        String header = b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long exp = System.currentTimeMillis() + ttlMs;
        String payload = b64(String.format("{\"sub\":\"%s\",\"type\":\"%s\",\"exp\":%d}", subject, type, exp));
        return header + "." + payload + "." + sign(header + "." + payload);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new RuntimeException("Failed to sign token", e); }
    }

    private String b64(String json) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private String decodePayload(String token) {
        return new String(Base64.getUrlDecoder().decode(token.split("\\.")[1]), StandardCharsets.UTF_8);
    }

    private String extractField(String json, String key) {
        String search = "\"" + key + "\":";
        int i = json.indexOf(search);
        if (i < 0) throw new IllegalArgumentException("Field not found: " + key);
        int start = i + search.length();
        char first = json.charAt(start);
        if (first == '"') { int end = json.indexOf('"', start + 1); return json.substring(start + 1, end); }
        else { int end = json.indexOf(',', start); if (end < 0) end = json.indexOf('}', start); return json.substring(start, end).trim(); }
    }
}
