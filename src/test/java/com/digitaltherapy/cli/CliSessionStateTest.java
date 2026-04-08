package com.digitaltherapy.cli;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CliSessionStateTest {

    @Test
    void isLoggedIn_returnsFalse_whenEmpty() {
        CliSessionState state = new CliSessionState();
        assertThat(state.isLoggedIn()).isFalse();
    }

    @Test
    void isLoggedIn_returnsTrue_whenTokenAndIdSet() {
        CliSessionState state = new CliSessionState();
        state.setUserId(UUID.randomUUID());
        state.setAccessToken("some-token");
        assertThat(state.isLoggedIn()).isTrue();
    }

    @Test
    void isLoggedIn_returnsFalse_whenOnlyTokenSet() {
        CliSessionState state = new CliSessionState();
        state.setAccessToken("some-token");
        assertThat(state.isLoggedIn()).isFalse();
    }

    @Test
    void clear_resetsAllFields() {
        CliSessionState state = new CliSessionState();
        state.setUserId(UUID.randomUUID());
        state.setUserName("Alice");
        state.setAccessToken("access");
        state.setRefreshToken("refresh");
        state.setActiveUserSessionId(UUID.randomUUID());

        state.clear();

        assertThat(state.isLoggedIn()).isFalse();
        assertThat(state.getUserName()).isNull();
        assertThat(state.getRefreshToken()).isNull();
        assertThat(state.getActiveUserSessionId()).isNull();
    }

    @Test
    void setters_andGetters_workCorrectly() {
        CliSessionState state = new CliSessionState();
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        state.setUserId(userId);
        state.setUserName("Bob");
        state.setAccessToken("acc");
        state.setRefreshToken("ref");
        state.setActiveUserSessionId(sessionId);

        assertThat(state.getUserId()).isEqualTo(userId);
        assertThat(state.getUserName()).isEqualTo("Bob");
        assertThat(state.getAccessToken()).isEqualTo("acc");
        assertThat(state.getRefreshToken()).isEqualTo("ref");
        assertThat(state.getActiveUserSessionId()).isEqualTo(sessionId);
    }
}
