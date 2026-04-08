package com.digitaltherapy.cli;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class CliSessionState {
    private UUID userId;
    private String userName;
    private String accessToken;
    private String refreshToken;
    private UUID activeUserSessionId;

    public boolean isLoggedIn() { return accessToken != null && userId != null; }

    public void clear() {
        userId = null; userName = null; accessToken = null;
        refreshToken = null; activeUserSessionId = null;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public UUID getActiveUserSessionId() { return activeUserSessionId; }
    public void setActiveUserSessionId(UUID activeUserSessionId) { this.activeUserSessionId = activeUserSessionId; }
}
